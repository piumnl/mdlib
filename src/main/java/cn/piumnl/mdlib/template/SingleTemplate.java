package cn.piumnl.mdlib.template;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.util.FileUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class SingleTemplate extends AbstractLibraryTemplate {

    private String content;

    public SingleTemplate(Site site, String name, String content) {
        super(site, name);
        this.content = content;
    }

    public SingleTemplate(Site site, File file) throws IOException {
        this(site, file.getName(), FileUtil.renderContent(FileUtil.readFile(file)));
    }
    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> listLibMap = new HashMap<>(5);
        listLibMap.put("uri", getSite().getUri());
        listLibMap.put("icon", getSite().getIcon());
        listLibMap.put("title", getName());
        listLibMap.put("siteName", getSite().getName());
        listLibMap.put("libraries", getSite().getLibraries());
        listLibMap.put("content", content);

        return listLibMap;
    }

    @Override
    public String ftlPath() {
        return "single.ftl";
    }

    public String getContent() {
        return content;
    }
}
