package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            writeFile(site, lib.getUrl(), renderContent);
            fileInfo.put(file.getAbsolutePath(), file.lastModified());
        }
    }

    @Override
    public void process(Site site) throws IOException {
        fileInfo = new HashMap<>(site.getSingle().size());
        for (Library lib : site.getSingle()) {
            File file = getSingleFile(lib);
            String renderContent = FileUtil.render(new SingleTemplate(site, file));
            writeFile(site, lib.getUrl(), renderContent);
            fileInfo.put(file.getAbsolutePath(), file.lastModified());
        }
    }

    /**
     * 将源文件渲染到目标文件中
     * @param site
     * @param filePath
     * @param renderContent
     * @throws IOException
     */
    private void writeFile(Site site, String filePath, String renderContent) throws IOException {
        Path resolve = resolvePath(site, filePath);
        LoggerUtil.PROCESSOR_LOGGER.info("渲染文件：" + resolve.toAbsolutePath().normalize().toString());
        Files.deleteIfExists(resolve);
        FileUtil.createFile(resolve);
        Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 通过 {@link Library} 对象获取单文件的 {@link File} 对象
     * @param lib {@link Library} 库
     * @return 指定文件的 File 对象
     */
    private File getSingleFile(Library lib) {
        List<String> dir = lib.getDir();
        if (dir.size() != 1) {
            String message = StringUtil.format("single 名字为 '{}' 的值应该只有一个！但是找到 {}！请勿使用逗号分隔",
                    lib.getName(), lib.getDir().size());
            throw new RuntimeException(message);
        } else {
            File file = new File(dir.get(0));
            if (file.isDirectory()) {
                throw new RuntimeException("single 的值应该为一个文件，而不是目录。");
            }

            return file;
        }
    }
}
