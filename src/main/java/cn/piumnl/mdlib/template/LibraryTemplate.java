package cn.piumnl.mdlib.template;

import java.util.Map;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public interface LibraryTemplate {

    /**
     * 模板文件所需要的变量值
     * @return 模板文件所需要的变量值
     */
    Map<String, Object> dataModel();

    /**
     * 页面对应的模板位置
     * @return 页面对应的模板位置
     */
    String ftlPath();

    /**
     * 站点信息
     * @return 站点信息类对象
     */
    Site getSite();

    /**
     * 页面标题
     * @return 页面标题
     */
    String getName();
}
