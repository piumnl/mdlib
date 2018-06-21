package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.index.SingleTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class SingleHandler extends AbstractLibraryTemplateHandler {

    @Override
    public void process(Site site) throws IOException {
        for (Library lib : site.getSingle()) {

            List<String> dir = lib.getDir();
            File file;
            if (dir.size() != 1) {
                throw new RuntimeException(
                        StringUtil.format("lib '{}' must be only one! find {}!",
                                lib.getName(), lib.getDir().size()));
            } else {
                file = new File(dir.get(0));
            }

            String renderContent = FileUtil.render(new SingleTemplate(site, file));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());
            LoggerUtil.PROCESSOR_LOGGER.info(resolve.toAbsolutePath().normalize().toString());
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
