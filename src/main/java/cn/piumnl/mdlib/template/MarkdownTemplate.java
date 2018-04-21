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
public class MarkdownTemplate extends AbstractLibraryTemplate {

    private String content;

    public MarkdownTemplate(Site site, String name, String content) {
        super(site, name, "markdown.ftl");
        this.content = content;
    }

    public MarkdownTemplate(Site site, File file) throws IOException {
        this(site, file.getName(), FileUtil.renderContent(FileUtil.readFile(file)));
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> map = new HashMap<>(4);

        initSiteModal(map);
        map.put("content", content);

        return map;
    }

    public String getContent() {
        return content;
    }
}
