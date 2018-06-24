package cn.piumnl.mdlib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import cn.piumnl.mdlib.template.LibraryTemplate;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class FileUtil {

    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

    static {
        // 初始化 FreeMarker 模板
        cfg.setClassForTemplateLoading(FileUtil.class, "/template");
        cfg.setOutputEncoding("UTF-8");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isJar() {
        try {
            URI uri = FileUtil.class.getResource("").toURI();
            String scheme = uri.getScheme();

            return "jar".equals(scheme);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generatedOutPath(Path fileRootPath, Path path, Path outPath) {
        Path relativize = fileRootPath.relativize(path);
        Path fileOutpath = outPath.resolve(relativize);

        String absolutePath = fileOutpath.toAbsolutePath().normalize().toFile().getPath();
        int i = absolutePath.lastIndexOf('.');
        return absolutePath.substring(0, i);
    }

    public static String classPath() {
        try {
            return FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static File classPath(String file) {
        return new File(classPath() + File.separator + file);
    }

    //region 文件操作

    /**
     * 复制目录
     * @param sourcePath 源文件目录
     * @param targetPath 目标文件目录
     * @throws IOException
     */
    public static void copyDirectory(File sourcePath, Path targetPath) throws IOException {
        copyDirectory(sourcePath, targetPath, FileUtil::copy);
    }

    /**
     * 两个文件进行复制
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException IO 读写异常
     */
    public static void copy(File source, File target) throws IOException {
        Files.copy(source.toPath(), target.toPath());
    }

    /**
     * 复制目录
     * @param sourcePath 源文件目录
     * @param targetPath 目标文件目录
     * @param consumer 如何复制文件
     * @throws IOException
     */
    public static void copyDirectory(File sourcePath, Path targetPath, FileFuncational<File> consumer)
            throws IOException {
        copyDirectory(sourcePath, targetPath, consumer, true);
    }

    /**
     * 复制目录
     * @param sourcePath 源文件目录
     * @param targetPath 目标文件目录
     * @param consumer 如何复制文件
     * @param existNeedDelete 是否需要判断删除文件
     * @throws IOException
     */
    public static void copyDirectory(File sourcePath, Path targetPath, FileFuncational<File> consumer,
                                     boolean existNeedDelete)
            throws IOException {
        //源目录不存在
        if (!sourcePath.exists()) {
            throw new FileNotFoundException(StringUtil.format("source path '{}' not found",
                    sourcePath.getAbsolutePath()));
        }

        if (Files.notExists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        File[] collect = sourcePath.listFiles();
        if (collect == null) {
            return;
        }
        for (File path : collect) {
            Path target = Paths.get(targetPath.toString(), path.getName());
            if (!path.isDirectory()) {
                if (existNeedDelete) {
                    if (Files.exists(target)) {
                        Files.delete(target);
                    }
                }

                consumer.accept(path, target.toFile());
            } else {
                copyDirectory(path, target, consumer, existNeedDelete);
            }
        }
    }

    /**
     * 创建单个文件，如果父级不存在就创建父级目录
     * @param resolve 目录对象
     * @throws IOException 当发生 IO 错误时抛出
     */
    public static void createFile(Path resolve) throws IOException {
        if (Files.notExists(resolve)) {
            Path parent = resolve.getParent();
            if (parent == null) {
                parent = resolve.toAbsolutePath().getParent();
            }

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            Files.createFile(resolve);
        } else {
            LoggerUtil.MDLIB_LOGGER.info(StringUtil.format("file {} is exists!", resolve.toAbsolutePath()));
        }
    }

    /**
     * 删除目录下所有内容，不包含目录本身
     * @param root 目录对象
     */
    public static void deleteDirectory(File root) {
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                if (f.exists()) {
                    deleteFile(f);
                }
            }
        }
    }

    /**
     * 删除单个文件或目录
     * @param f 文件或目录对象
     */
    private static void deleteFile(File f) {
        deleteDirectory(f);
        if (!f.delete()) {
            LoggerUtil.MDLIB_LOGGER.warning(StringUtil.format("can't delete {}", f.getAbsolutePath()));
        }
    }

    /**
     * 读取文件为一个字符串
     * @param file 文件对象
     * @return 该文件的所有数据
     * @throws IOException 当读取文件出现 IO 问题时抛出
     */
    public static String readFile(File file) throws IOException {
        LoggerUtil.MDLIB_LOGGER.debug(StringUtil.format("read file '{}'!", file.getAbsolutePath()));
        List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        for (String line : allLines) {
            builder.append(line).append('\n');
        }

        return builder.toString();
    }

    //endregion

    //region 渲染操作

    /**
     * 渲染模板
     * @param template 渲染页面所需要的内容
     * @return 渲染后的页面内容
     * @throws IOException 当写入数据到文件出现的 IO 写入错误时抛出
     */
    public static String render(LibraryTemplate template) throws IOException {
        return render(template.dataModel(), template.ftlPath());
    }

    /**
     * freemarker 页面渲染
     *
     * @param dataModel 页面数据
     * @param ftlPath   ftl 页面路径
     * @return 渲染后的页面
     * @throws IOException 当写入数据到文件出现的 IO 写入错误时抛出
     */
    public static String render(Object dataModel, String ftlPath) throws IOException {
        Template template = cfg.getTemplate(ftlPath);
        StringWriter out = new StringWriter();
        try {
            template.process(dataModel, out);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    /**
     * 渲染 md 文档
     * @param content md 文档内容
     * @return 渲染后的内容
     */
    public static String renderContent(String content) {
        // markdown to image
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(content.replaceAll("\r\n", "\n"));
        return renderer.render(document);
    }

    //endregion
}
