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
     * 刷新操作
     *
     * @param site
     * @throws Exception
     */
    void refresh(Site site) throws Exception;

    /**
     * 初始化操作
     *
     * @param site
     * @throws Exception
     */
    static void initHandler(Site site) throws Exception {
        // 复制静态资源
        StaticHandler.getInstance().process(site);
        // List 配置
        ListHandler.getInstance().process(site);
        // Collapsible 配置
        CollapsibleHandler.getInstance().process(site);
        // Single 配置
        SingleHandler.getInstance().process(site);
        // code 处理
        CodeTreeHandler.getInstance().process(site);
        // 首页处理
        IndexHandler.getInstance().process(site);
    }

    /**
     * 刷新操作
     *
     * @param site
     * @throws Exception
     */
    static void refreshHandler(Site site) throws Exception {
        // 复制静态资源
        StaticHandler.getInstance().refresh(site);
        // List 配置
        ListHandler.getInstance().refresh(site);
        // Collapsible 配置
        CollapsibleHandler.getInstance().refresh(site);
        // Single 配置
        SingleHandler.getInstance().refresh(site);
        // code 处理
        CodeTreeHandler.getInstance().refresh(site);
        // 首页处理
        IndexHandler.getInstance().process(site);
    }
}
