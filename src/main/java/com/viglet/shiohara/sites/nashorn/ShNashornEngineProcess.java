package com.viglet.shiohara.sites.nashorn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viglet.shiohara.sites.cache.component.ShCacheJavascript;

@Component
public class ShNashornEngineProcess {
	private static final Log logger = LogFactory.getLog(ShNashornEngineProcess.class);
	private static final ThreadLocal<HashMap<String, ScriptContext>> scriptContexts = new ThreadLocal<>();
	@Autowired
	private ScriptEngine scriptEngine;
	@Autowired
	private ShCacheJavascript shCacheJavascript;

	public Object render(String objectName, String javascript, String html, HttpServletRequest request,
			Map<String, Object> shContent) {
		try {
			ScriptContext sc = new SimpleScriptContext();
			SimpleScriptContext ssc = new SimpleScriptContext();
			ssc.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
			Bindings b = scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE);
			if (b != null) {
				for (Map.Entry<String, Object> e : b.entrySet()) {
					ssc.setAttribute((String) e.getKey(), e.getValue(), ScriptContext.ENGINE_SCOPE);
				}
			}
			sc = shObjectLib(ssc);
			sc.setAttribute("shContent", shContent, ScriptContext.ENGINE_SCOPE);
			sc.setAttribute("html", html, ScriptContext.ENGINE_SCOPE);
			sc.setAttribute("request", request, ScriptContext.ENGINE_SCOPE);

			return scriptEngine.eval(javascript, sc);

		} catch (Throwable err) {
			regionError(objectName, javascript, err);
		}
		return null;
	}

	private ScriptContext shObjectLib(SimpleScriptContext ssc) throws IOException, ScriptException {
		StringBuilder shObjectJS;
		ScriptContext sc;
		if (scriptContexts.get() == null) {
			logger.debug("Creating TL");
			scriptContexts.set(new HashMap<String, ScriptContext>());
		}
		if ((sc = scriptContexts.get().get("shScript")) != null) {
			logger.debug("Reusing shScript");
		} else {
			logger.debug("Creating shScript");

			shObjectJS = shCacheJavascript.shObjectJSFactory();
			scriptEngine.eval(shObjectJS.toString(), ssc);
			HashMap<String, ScriptContext> elementMap = new HashMap<>();
			sc = (ScriptContext) ssc;
			elementMap.put("shScript", sc);
			scriptContexts.set(elementMap);

		}
		return sc;
	}

	public void regionError(String regionAttr, String javascript, Throwable err) {
		if (err instanceof ScriptException) {
			ScriptException exc = ((ScriptException) err);
			String scriptStack = ExceptionUtils.getStackTrace(exc);
			int columnNumber = exc.getColumnNumber();
			int lineNumber = exc.getLineNumber();
			String fileName = exc.getFileName();
			String message = exc.getMessage();
			String[] javascriptLines = javascript.split("\\n");
			StringBuffer errorCode = new StringBuffer();
			int minlines = 0;
			if (lineNumber - 5 > minlines) {
				minlines = lineNumber - 5;
			}
			int maxlines = javascriptLines.length;
			
			if (lineNumber + 5 < maxlines)
				maxlines = lineNumber + 5;			
			for (int x = minlines; x <= maxlines; x++) {
				errorCode.append(javascriptLines[x] + "\n");
				if (x == lineNumber - 1) {
					String errorPos = IntStream.range(0, columnNumber).mapToObj(i -> "-")
							.collect(Collectors.joining("")) + "^";
					errorCode.append(errorPos + "\n");
				}

			}
			logger.error(String.format("Javascript Code of %s:\n %s", regionAttr, errorCode));
			logger.error(String.format("ScriptError: %s '%s' at: <%s>%d:%d\n%s", regionAttr, message, fileName,
					lineNumber, columnNumber, scriptStack));
		} else {
			logger.error((ExceptionUtils.getStackTrace(err)));
		}
	}
}
