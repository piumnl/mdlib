package cn.piumnl.mdlib.handler;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public interface Handler {

    /**
     * 处理模板
     * @param site
     * @throws Exception
     */
    void process(Site site) throws Exception;

    /**
     * @param site
     * @throws Exception
     */
    static void initHandler(Site site) throws Exception {
        // 复制静态资源
        new StaticHandler().process(site);
        // 渲染 md
        new MarkdownHandler().process(site);
        // List 配置
        new ListHandler().process(site);
        // Collapsible 配置
        new CollapsibleHandler().process(site);
        // Single 配置
        new SingleHandler().process(site);
        // code 处理
        new CodeTreeHandler().process(site);
    }
}
