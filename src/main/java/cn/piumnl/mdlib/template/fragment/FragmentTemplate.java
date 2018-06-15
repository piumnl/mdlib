package cn.piumnl.mdlib.template.fragment;

import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.AbstractLibraryTemplate;

/**
 * 代码片段模板
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-04.
 */
public class FragmentTemplate extends AbstractLibraryTemplate {

    private final String language;

    private final String content;

    private final String extName;

    public FragmentTemplate(Site site, String name, String content) {
        super(site, name, "fragment.ftl");
        this.language = name.substring(name.indexOf(".") + 1, name.length());
        this.content = content;
        this.extName = this.language;
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> listLibMap = new HashMap<>(6);

        initSiteModal(listLibMap);
        listLibMap.put("language", language);
        listLibMap.put("content", content);
        listLibMap.put("extName", extName);

        return listLibMap;
    }
}
