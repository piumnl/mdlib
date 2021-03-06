package cn.piumnl.mdlib.template.md;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.RenderFile;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.AbstractLibraryTemplate;
import cn.piumnl.mdlib.util.FileUtil;

/**
 * 文章模板
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class MarkdownTemplate extends AbstractLibraryTemplate {

    private String content;

    private String lastModifiedTime;

    public MarkdownTemplate(Site site, String name, String content, long lastModifiedTime) {
        super(site, name, "markdown.ftl");
        this.content = content;
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(lastModifiedTime).atZone(ZoneId.systemDefault());
        this.lastModifiedTime = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(zonedDateTime);
    }

    public MarkdownTemplate(Site site, RenderFile renderFile, String name, long lastModifiedTime) {
        this(site, renderFile.hasTitle() ? renderFile.getTitle() : name, renderFile.getContent(), lastModifiedTime);
    }

    public MarkdownTemplate(Site site, File file) throws IOException {
        this(site, FileUtil.renderContent(FileUtil.readFile(file)), file.getName(), file.lastModified());
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> map = new HashMap<>(4);

        initSiteModal(map);
        map.put("content", content);
        map.put("lastModifiedTime", lastModifiedTime);

        return map;
    }

    public String getContent() {
        return content;
    }
}
