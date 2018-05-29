package com.viglet.shiohara.onstartup.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.viglet.shiohara.object.ShObjectType;
import com.viglet.shiohara.persistence.model.globalid.ShGlobalId;
import com.viglet.shiohara.persistence.model.post.type.ShPostType;
import com.viglet.shiohara.persistence.model.user.ShUser;
import com.viglet.shiohara.persistence.repository.globalid.ShGlobalIdRepository;
import com.viglet.shiohara.persistence.repository.post.type.ShPostTypeRepository;
import com.viglet.shiohara.persistence.repository.user.ShUserRepository;
import com.viglet.shiohara.post.type.ShSystemPostType;

@Component
public class ShUserOnStartup {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ShUserRepository shUserRepository;
	@Autowired
	private ShPostTypeRepository shPostTypeRepository;
	@Autowired
	private ShGlobalIdRepository shGlobalIdRepository;
	
	public void createDefaultRows() {

		if (shUserRepository.findAll().isEmpty()) {
			ShPostType shPostType = shPostTypeRepository.findByName(ShSystemPostType.TEXT.toString());

			ShUser shUser = new ShUser();

			shUser.setEmail("admin@localhost.local");
			shUser.setFirstName("Admin");
			shUser.setLastLogin(new Date());
			shUser.setLastName("Administrator");
			shUser.setLastPostType(String.valueOf(shPostType.getId()));
			shUser.setLoginTimes(0);
			shUser.setPassword(passwordEncoder.encode("admin"));
			shUser.setRealm("default");
			shUser.setUsername("admin");
			shUser.setEnabled(1);
			
			ShGlobalId shGlobalId = new ShGlobalId();
			shGlobalId.setType(ShObjectType.USER);
			shPostType.setShGlobalId(shGlobalId);

			shUserRepository.save(shUser);
			
			shUser = new ShUser();

			shUser.setEmail("sample@localhost.local");
			shUser.setFirstName("Sample user");
			shUser.setLastLogin(new Date());
			shUser.setLastName("Sample");
			shUser.setLastPostType(String.valueOf(shPostType.getId()));
			shUser.setLoginTimes(0);
			shUser.setPassword(passwordEncoder.encode("sample123"));
			shUser.setRealm("default");
			shUser.setUsername("sample");
			shUser.setEnabled(1);
			
			shGlobalId = new ShGlobalId();
			shGlobalId.setType(ShObjectType.POST_TYPE);
			shPostType.setShGlobalId(shGlobalId);

			shUserRepository.save(shUser);			
		}

	}
}
