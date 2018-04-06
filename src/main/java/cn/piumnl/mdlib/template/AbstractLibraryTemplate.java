package cn.piumnl.mdlib.template;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public abstract class AbstractLibraryTemplate implements LibraryTemplate {

    private Site site;

    private String name;

    public AbstractLibraryTemplate(Site site, String name) {
        this.site = site;
        this.name = name;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public String getName() {
        return name;
    }
}
