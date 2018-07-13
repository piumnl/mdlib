package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.index.SingleTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class SingleHandler extends AbstractLibraryTemplateHandler {

    private static final SingleHandler HANDLER = new SingleHandler();

    private Map<String, Long> fileInfo;

    private SingleHandler() {
    }

    public static SingleHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void refresh(Site site) throws Exception {
        for (Library lib : site.getSingle()) {
            File file = getSingleFile(lib);

            Long lastModified = fileInfo.get(file.getAbsolutePath());
            if (lastModified != null) {
                if (file.lastModified() <= lastModified) {
                    continue;
                }
            }

            String renderContent = FileUtil.render(new SingleTemplate(site, file));
            writeFile(site.getOut().resolve(lib.getUrl()), renderContent);
            fileInfo.put(file.getAbsolutePath(), file.lastModified());
        }
    }

    @Override
    public void process(Site site) throws IOException {
        fileInfo = new HashMap<>(site.getSingle().size());
        for (Library lib : site.getSingle()) {
            File file = getSingleFile(lib);
            String renderContent = FileUtil.render(new SingleTemplate(site, file));
            writeFile(site.getOut().resolve(lib.getUrl()), renderContent);
            fileInfo.put(file.getAbsolutePath(), file.lastModified());
        }
    }

    /**
     * 将源文件渲染到目标文件中
     * @param writeFile 要写入的文件
     * @param renderContent 渲染的内容
     * @throws IOException IO 异常
     */
    private void writeFile(Path writeFile, String renderContent) throws IOException {
        LoggerUtil.PROCESSOR_LOGGER.info("渲染文件：{}", writeFile.toAbsolutePath().normalize());
        Files.deleteIfExists(writeFile);
        FileUtil.createFile(writeFile);
        Files.write(writeFile, renderContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 通过 {@link Library} 对象获取单文件的 {@link File} 对象
     * @param lib {@link Library} 库
     * @return 指定文件的 File 对象
     */
    private File getSingleFile(Library lib) {
        File file = new File(lib.getDir());
        if (file.isDirectory()) {
            throw new RuntimeException("single 的值应该为一个文件，而不是目录。");
        }

        return file;
    }
}
