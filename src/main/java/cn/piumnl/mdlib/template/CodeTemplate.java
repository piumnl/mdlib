package cn.piumnl.mdlib.template;

import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-23.
 */
public class CodeTemplate extends AbstractLibraryTemplate {

    private String tree;

    public CodeTemplate(Site site, String name, String tree) {
        super(site, name, "code.ftl");
        this.tree = tree;
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> listLibMap = new HashMap<>(6);

        initSiteModal(listLibMap);
        listLibMap.put("siteName", getSite().getName());
        listLibMap.put("libraries", getSite().getLibraries());
        listLibMap.put("tree", tree);

        return listLibMap;
    }
}
