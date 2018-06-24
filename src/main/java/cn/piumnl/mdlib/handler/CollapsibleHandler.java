package cn.piumnl.mdlib.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.piumnl.mdlib.entity.ArchiveIndex;
import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.index.CollapsibleTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class CollapsibleHandler extends AbstractLibraryTemplateHandler {

    private static final CollapsibleHandler HANDLER = new CollapsibleHandler();

    private CollapsibleHandler() {
    }

    public static CollapsibleHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void refresh(Site site) throws Exception {
        // todo for piumnl: refresh
    }

    @Override
    public void process(Site site) throws IOException {
        for (Library lib : site.getCollapsible()) {
            // 指定要生成的目录
            List<Article> collect = getArticles(site, lib);

            // 渲染
            // 指定一个 8 ，一般可能不会超过 8，默认为 16
            Map<String, List<Article>> map = new HashMap<>(8);
            for (Article article : collect) {
                int end = article.getUrl().lastIndexOf("\\");
                if (end == -1) {
                    throw new RuntimeException("start is -1 in " + article);
                }
                int start = article.getUrl().substring(0, end).lastIndexOf("\\") + 1;
                if (!lib.getName().equals(article.getUrl().substring(start, end))) {
                    String key = article.getUrl().substring(start, end);
                    List<Article> articles = map.computeIfAbsent(key, k -> new ArrayList<>());
                    articles.add(article);
                } else {
                    List<Article> articles = map.computeIfAbsent("默认", k -> new ArrayList<>());
                    articles.add(article);
                }
            }

            List<ArchiveIndex> archiveIndices =
                    map.entrySet()
                       .stream()
                       .map(entry -> new ArchiveIndex(entry.getKey(), entry.getValue()))
                       .sorted().collect(Collectors.toList());
            String renderContent = FileUtil.render(new CollapsibleTemplate(site, lib.getName(), archiveIndices));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());
            LoggerUtil.PROCESSOR_LOGGER.info(resolve.toAbsolutePath().normalize().toFile().getPath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));
        }

    }
}
