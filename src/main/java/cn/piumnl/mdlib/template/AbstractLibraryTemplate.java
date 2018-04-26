package cn.piumnl.mdlib.template;

import java.util.Map;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public abstract class AbstractLibraryTemplate implements LibraryTemplate {

    private static final String MD_SUFFIX = ".md";

    private Site site;

    private String ftlPath;

    private String name;

    public AbstractLibraryTemplate(Site site, String name, String ftlPath) {
        this.site = site;
        this.name = name;
        this.ftlPath = ftlPath;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String ftlPath() {
        return ftlPath;
    }

    protected void initSiteModal(Map<String, Object> map) {
        map.put("uri", getSite().getUri());
        map.put("icon", getSite().getIcon());
        map.put("title", getName().endsWith(MD_SUFFIX) ? noSuffixName() : getName());
    }

    private String noSuffixName() {
        return getName().substring(0, getName().length() - MD_SUFFIX.length());
    }
}
