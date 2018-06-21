package cn.piumnl.mdlib.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.index.ListTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class ListHandler extends AbstractLibraryTemplateHandler {

    @Override
    public void process(Site site) throws IOException {
        for (Library lib : site.getList()) {
            List<Article> collect = getArticles(site, lib, site.getAllFile());

            Collections.sort(collect);

            // 渲染
            String renderContent = FileUtil.render(new ListTemplate(site, lib.getName(), collect));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());

            LoggerUtil.PROCESSOR_LOGGER.info(resolve.toAbsolutePath().normalize().toFile().getPath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
