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

    /**
     * 创建一个模板对象
     * @param site 站点信息
     * @param title 页面标题
     * @param ftlPath 页面对应的模板位置
     */
    public AbstractLibraryTemplate(Site site, String title, String ftlPath) {
        this.site = site;
        this.name = title;
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

    /**
     * 初始化三个默认的参数
     * @param map 在此 Map 上追加
     */
    protected void initSiteModal(Map<String, Object> map) {
        map.put("uri", getSite().getUri());
        map.put("icon", getSite().getIcon());
        map.put("title", getName().endsWith(MD_SUFFIX) ? noSuffixName() : getName());
    }

    /**
     * 删除 md 文件时传入的文件名后缀
     * @return readme.md => readme
     */
    private String noSuffixName() {
        return getName().substring(0, getName().length() - MD_SUFFIX.length());
    }
}
