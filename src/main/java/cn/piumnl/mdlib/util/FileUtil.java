package cn.piumnl.mdlib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Stream;

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

import org.apache.commons.io.FilenameUtils;

import cn.piumnl.mdlib.template.LibraryTemplate;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class FileUtil {

    private static Logger logger = Logger.getLogger("cn.piumnl.mdlib");

    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

    static {
        // 初始化 FreeMarker 模板
        cfg.setClassForTemplateLoading(FileUtil.class, "/template");
        cfg.setOutputEncoding("UTF-8");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private FileUtil() {
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

    public static JarFile getJar(URL url) {
        try {
            return ((JarURLConnection) url.openConnection()).getJarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFolder(File sourcePath, Path targetPath) throws IOException {
        copyFolder(sourcePath, targetPath, com.google.common.io.Files::copy);
    }

    public static void copyFolder(File sourcePath, Path targetPath, FileFuncational<File> consumer)
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
                if (Files.exists(target)) {
                    Files.delete(target);
                }

                consumer.accept(path, target.toFile());
            } else {
                copyFolder(path, target);
            }
        }
    }

    /**
     * freemarker 页面渲染
     *
     * @param dataModel 页面数据
     * @param ftlPath   ftl 页面路径
     * @return 渲染后的页面
     * @throws IOException       .
     * @throws TemplateException .
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
     * 渲染模板
     * @param template 渲染页面所需要的内容
     * @return 渲染后的页面内容
     * @throws IOException
     */
    public static String render(LibraryTemplate template) throws IOException {
        return render(template.dataModel(), template.ftlPath());
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

    public static String generatedOutPath(Path fileRootPath, Path path, Path outPath) {
        Path relativize = fileRootPath.relativize(path);
        Path fileOutpath = outPath.resolve(relativize);

        String absolutePath = fileOutpath.toFile().getAbsolutePath();
        int i = absolutePath.lastIndexOf('.');
        return absolutePath.substring(0, i);
    }

    public static String readFile(File file) throws IOException {
        logger.fine(StringUtil.format("read file '{}'!", file.getAbsolutePath()));
        List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        for (String line : allLines) {
            builder.append(line).append('\n');
        }

        return builder.toString();
    }

    public static Path getPath(Path path) {
        URL resource = FileUtil.class.getResource("/");
        try {
            Path rootPath = Paths.get(resource.toURI());
            return rootPath.resolve(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteAllFiles(File root) {
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

    private static void deleteFile(File f) {
        deleteAllFiles(f);
        if (!f.delete()) {
            RefelectUtil.LOGGER.warning(StringUtil.format("can't delete {}", f.getAbsolutePath()));
        }
    }

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
            RefelectUtil.LOGGER.info(StringUtil.format("file {} is exists!", resolve.toAbsolutePath()));
        }
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

    public static boolean isBinary(String file) {
        String extension = FilenameUtils.getExtension(file).toLowerCase();


        return Stream.of("png", "jpg", "jpeg", "bmp", "gif", "svg", "gif", "ico", "woff", "woff2")
                     .anyMatch(s -> s.equals(extension));
    }
}
