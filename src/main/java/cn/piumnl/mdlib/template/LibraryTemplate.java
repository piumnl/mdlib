package cn.piumnl.mdlib.template;

import java.util.Map;

import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public interface LibraryTemplate {

    Map<String, Object> dataModel();

    String ftlPath();

    Site getSite();

    String getName();
}
