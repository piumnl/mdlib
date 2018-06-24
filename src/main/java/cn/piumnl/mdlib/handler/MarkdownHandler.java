package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.md.MarkdownTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class MarkdownHandler implements Handler {

    private static final MarkdownHandler HANDLER = new MarkdownHandler();

    private Map<String, Long> fileInfo;

    private MarkdownHandler() {
    }

    public static MarkdownHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void refresh(Site site) throws Exception {
        site.getLibraries()
            .stream()
            .flatMap(library -> library.getDir().stream())
            .distinct()
            .forEach(s -> renderMd(Paths.get(s).toAbsolutePath(), site));
    }

    @Override
    public void process(Site site) {
        fileInfo = new HashMap<>();
        site.getLibraries()
            .stream()
            .flatMap(library -> library.getDir().stream())
            .distinct()
            .forEach(p -> renderMd(Paths.get(p).toAbsolutePath(), site));
    }

    private void renderMd(Path path, Site site) {
        try {
            renderMd(path.toFile(), path.getParent(), site);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderMd(File file, Path root, Site site) throws IOException {
        File[] subFiles = file.listFiles();

        if (subFiles == null) {
            return;
        }

        for (File subFile : subFiles) {
            if (subFile.isDirectory()) {
                LoggerUtil.PROCESSOR_LOGGER.trace("进入目录： '{}'", subFile.getAbsolutePath());

                renderMd(subFile, root, site);
            } else {
                Long lastModified = fileInfo.get(subFile.getAbsolutePath());
                if (lastModified == null || lastModified < subFile.lastModified()) {
                    if (!subFile.getName().endsWith(".md")) {
                        return;
                    }

                    LoggerUtil.PROCESSOR_LOGGER.info("  渲染md文件： '{}'", Paths.get(subFile.getAbsolutePath()).normalize());

                    String outPath = FileUtil.generatedOutPath(root, subFile.toPath(), site.getOut()) + ".html";
                    Path resolve = site.getOut().resolve(outPath);
                    FileUtil.createFile(resolve);

                    String renderContent = FileUtil.render(new MarkdownTemplate(site, subFile));
                    Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

                    fileInfo.put(subFile.getAbsolutePath(), subFile.lastModified());
                    LoggerUtil.PROCESSOR_LOGGER.info("          => '{}'", Paths.get(outPath).normalize());
                }
            }
        }
    }
}
