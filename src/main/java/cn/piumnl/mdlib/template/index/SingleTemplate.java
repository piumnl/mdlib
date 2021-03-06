package cn.piumnl.mdlib.template.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.RenderFile;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.AbstractLibraryTemplate;
import cn.piumnl.mdlib.util.FileUtil;

/**
 * 单文件模板
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class SingleTemplate extends AbstractLibraryTemplate {

    private String content;

    public SingleTemplate(Site site, String name, String content) {
        super(site, name, "single.ftl");
        this.content = content;
    }

    public SingleTemplate(Site site, RenderFile renderFile, String name) {
        this(site, renderFile.hasTitle() ? renderFile.getTitle() : name, renderFile.getContent());
    }

    public SingleTemplate(Site site, File file) throws IOException {
        this(site, FileUtil.renderContent(FileUtil.readFile(file)), file.getName());
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> listLibMap = new HashMap<>(6);

        initSiteModal(listLibMap);
        listLibMap.put("siteName", getSite().getName());
        listLibMap.put("libraries", getSite().getLibraries());
        listLibMap.put("content", content);

        return listLibMap;
    }

    public String getContent() {
        return content;
    }
}
