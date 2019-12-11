package com.viglet.shiohara.exchange.folder;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.viglet.shiohara.exchange.*;
import com.viglet.shiohara.exchange.post.ShPostExport;
import com.viglet.shiohara.exchange.post.type.ShPostTypeExport;
import com.viglet.shiohara.persistence.model.post.ShPost;
import com.viglet.shiohara.persistence.repository.post.ShPostAttrRepository;
import com.viglet.shiohara.persistence.repository.post.ShPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viglet.shiohara.exchange.post.ShPostImport;
import com.viglet.shiohara.persistence.model.folder.ShFolder;
import com.viglet.shiohara.persistence.model.site.ShSite;
import com.viglet.shiohara.persistence.repository.folder.ShFolderRepository;
import com.viglet.shiohara.persistence.repository.site.ShSiteRepository;
import com.viglet.shiohara.url.ShURLFormatter;

@Component
public class ShFolderImportExport {
    @Autowired
    private ShSiteRepository shSiteRepository;
    @Autowired
    private ShFolderRepository shFolderRepository;
    @Autowired
    private ShURLFormatter shURLFormatter;
    @Autowired
    private ShPostImport shPostImport;
    @Autowired
    private ShPostRepository shPostRepository;
    @Autowired
    private ShPostAttrRepository shPostAttrRepository;
    @Autowired
    private ShPostExport shPostExport;
    @Autowired
    private ShPostTypeExport shPostTypeExport;

    public void shFolderImportNested(String shObject, File extractFolder, String username, boolean importOnlyFolders,
                                     Map<String, Object> shObjects, Map<String, List<String>> shChildObjects) throws IOException {
        if (shChildObjects.containsKey(shObject)) {
            for (String objectId : shChildObjects.get(shObject)) {
                if (shObjects.get(objectId) instanceof ShFolderExchange) {
                    ShFolderExchange shFolderExchange = (ShFolderExchange) shObjects.get(objectId);

                    ShFolder shFolderChild = null;
                    Optional<ShFolder> shFolderOptional = shFolderRepository.findById(shFolderExchange.getId());
                    if (shFolderOptional.isPresent()) {
                        shFolderChild = shFolderOptional.get();
                    } else {
                        shFolderChild = new ShFolder();
                        shFolderChild.setId(shFolderExchange.getId());
                        shFolderChild.setDate(shFolderExchange.getDate());
                        shFolderChild.setName(shFolderExchange.getName());
                        if (shFolderExchange.getPosition() > 0) {
                            shFolderChild.setPosition(shFolderExchange.getPosition());
                        }
                        if (shFolderExchange.getOwner() != null) {
                            shFolderChild.setOwner(shFolderExchange.getOwner());
                        } else {
                            shFolderChild.setOwner(username);
                        }
                        if (shFolderExchange.getFurl() != null) {
                            shFolderChild.setFurl(shFolderExchange.getFurl());
                        } else {
                            shFolderChild.setFurl(shURLFormatter.format(shFolderExchange.getName()));
                        }
                        if (shFolderExchange.getParentFolder() != null) {
                            ShFolder parentFolder = shFolderRepository.findById(shFolderExchange.getParentFolder()).orElse(null);
                            shFolderChild.setParentFolder(parentFolder);
                            shFolderChild.setRootFolder((byte) 0);
                        } else {
                            if (shObjects.get(shObject) instanceof ShSiteExchange) {
                                ShSiteExchange shSiteExchange = (ShSiteExchange) shObjects.get(shObject);
                                if (shSiteExchange.getRootFolders().contains(shFolderExchange.getId())) {
                                    shFolderChild.setRootFolder((byte) 1);
                                    ShSite parentSite = shSiteRepository.findById(shSiteExchange.getId()).orElse(null);
                                    shFolderChild.setShSite(parentSite);
                                }
                            }
                        }
                        shFolderRepository.save(shFolderChild);
                    }

                    this.shFolderImportNested(shFolderChild.getId(), extractFolder, username, importOnlyFolders, shObjects,
                            shChildObjects);
                }

                if (!importOnlyFolders && shObjects.get(objectId) instanceof ShPostExchange) {
                    ShPostExchange shPostExchange = (ShPostExchange) shObjects.get(objectId);
                    shPostImport.createShPost(shPostExchange, extractFolder, username, shObjects);
                }
            }

        }
    }

    public ShExchange shFolderExchangeIterate(Set<ShFolder> shFolders) {
        ShExchange shExchange = new ShExchange();
        List<ShFolderExchange> shFolderExchanges = new ArrayList<ShFolderExchange>();
        List<ShPostExchange> shPostExchanges = new ArrayList<ShPostExchange>();
        List<ShFileExchange> files = new ArrayList<ShFileExchange>();
        Map<String, ShPostTypeExchange> shPostTypeExchanges = new HashMap<String, ShPostTypeExchange>();
        for (ShFolder shFolder : shFolders) {

            for (ShPost shPost : shPostRepository.findByShFolder(shFolder)) {
                
                ShPostExchange shPostExchange = new ShPostExchange();
                shPostExchange.setId(shPost.getId());
                shPostExchange.setFolder(shPost.getShFolder().getId());
                shPostExchange.setDate(shPost.getDate());
                shPostExchange.setPostType(shPost.getShPostType().getName());
                shPostExchange.setOwner(shPost.getOwner());
                shPostExchange.setFurl(shPost.getFurl());
                shPostExchange.setPosition(shPost.getPosition());

                if (!shPostTypeExchanges.containsKey(shPost.getShPostType().getName())) {
                    shPostTypeExchanges.put(shPost.getShPostType().getName(),
                            shPostTypeExport.exportPostType(shPost.getShPostType()));
                }
                Map<String, Object> fields = new HashMap<String, Object>();

                shPostExport.shPostAttrExchangeIterate(shPost, shPostAttrRepository.findByShPost(shPost), fields,
                        files);

                shPostExchange.setFields(fields);
                shPostExchanges.add(shPostExchange);
            }
            ShFolderExchange shFolderExchangeChild = new ShFolderExchange();
            shFolderExchangeChild.setId(shFolder.getId());
            shFolderExchangeChild.setDate(shFolder.getDate());
            shFolderExchangeChild.setName(shFolder.getName());
            shFolderExchangeChild.setOwner(shFolder.getOwner());
            shFolderExchangeChild.setFurl(shFolder.getFurl());
            shFolderExchangeChild.setPosition(shFolder.getPosition());

            if (shFolder.getParentFolder() != null) {
                shFolderExchangeChild.setParentFolder(shFolder.getParentFolder().getId());
            }
            shFolderExchanges.add(shFolderExchangeChild);

            Set<ShFolder> childFolders = shFolderRepository.findByParentFolder(shFolder);
            ShExchange shExchangeChild = this.shFolderExchangeIterate(childFolders);

            shFolderExchanges.addAll(shExchangeChild.getFolders());
            shPostExchanges.addAll(shExchangeChild.getPosts());

            for (ShPostTypeExchange shPostTypeExchange : shExchangeChild.getPostTypes()) {
                if (!shPostTypeExchanges.containsKey(shPostTypeExchange.getName())) {
                    shPostTypeExchanges.put(shPostTypeExchange.getName(), shPostTypeExchange);
                }

            }
            files.addAll(shExchangeChild.getFiles());
        }
        shExchange.setFolders(shFolderExchanges);
        shExchange.setPosts(shPostExchanges);
        shExchange.setFiles(files);
        shExchange.setPostTypes(new ArrayList<ShPostTypeExchange>(shPostTypeExchanges.values()));
        return shExchange;
    }

    private ShPostExchange exportShPost(List<ShFileExchange> files, Map<String, ShPostTypeExchange> shPostTypeExchanges,
                                        ShPost shPost) {
        ShPostExchange shPostExchange = new ShPostExchange();
        shPostExchange.setId(shPost.getId());
        shPostExchange.setFolder(shPost.getShFolder().getId());
        shPostExchange.setDate(shPost.getDate());
        shPostExchange.setPostType(shPost.getShPostType().getName());
        shPostExchange.setOwner(shPost.getOwner());
        shPostExchange.setFurl(shPost.getFurl());
        shPostExchange.setPosition(shPost.getPosition());

        if (!shPostTypeExchanges.containsKey(shPost.getShPostType().getName())) {
            shPostTypeExchanges.put(shPost.getShPostType().getName(),
                    shPostTypeExport.exportPostType(shPost.getShPostType()));
        }
        Map<String, Object> fields = new HashMap<String, Object>();

        shPostExport.shPostAttrExchangeIterate(shPost, shPostAttrRepository.findByShPost(shPost), fields,
                files);

        shPostExchange.setFields(fields);
        return shPostExchange;
    }
}
