package cn.piumnl.mdlib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import freemarker.template.TemplateException;

import cn.piumnl.mdlib.entity.ArchiveIndex;
import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.CollapsibleTemplate;
import cn.piumnl.mdlib.template.ListTemplate;
import cn.piumnl.mdlib.template.MarkdownTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class Processor {

    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());

    private Site site;

    private List<String> allFile = new ArrayList<>();

    public Processor(Site site) {
        this.site = site;
    }

    public void processor() throws IOException, TemplateException {
        // 删除输出目录所有文件
        deleteAllFiles(site.getOut());
        if (site.isDefaultStaticPath()) {
            if (FileUtil.isJar()) {
                try {
                    JarURLConnection connection =
                            (JarURLConnection) this.getClass().getResource("").toURI().toURL().openConnection();
                    JarFile jarFile = connection.getJarFile();
                    jarFile.stream()
                           .map(ZipEntry::getName)
                           .filter(name -> name.startsWith("static/") && !name.endsWith("/"))
                           .forEach(name -> copy(jarFile, name));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        // 复制静态资源
        copyComplexPath(site.getStaticPath(), site.getOut());
        // 渲染 md
        site.getLibraries()
            .stream()
            .flatMap(library -> library.getDir().stream())
            .distinct()
            .forEach(p -> renderMd(Paths.get(p)));

        // -----------------------------------------------

        // List 配置
        renderList();
        // Collapsible 配置
        renderCollapsible();
        // Single 配置
        renderSingle();
    }

    private void renderSingle() throws IOException, TemplateException {
        for (Library lib : site.getSingle()) {

            List<String> dir = lib.getDir();
            File file;
            if (dir.size() > 1) {
                throw new RuntimeException(
                        StringUtil.format("lib '{}' must be only one! find {}!",
                                lib.getName(), lib.getDir().size()));
            } else {
                file = new File(dir.get(0));
            }

            String renderContent = FileUtil.render(new MarkdownTemplate(site, file));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());
            LOGGER.info(resolve.toFile().getAbsolutePath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void copy(JarFile jarFile, String name) {
        URL url;
        try {
            url = new URI("jar:file:/" + jarFile.getName().replaceAll("\\\\", "/") + "!/" + name).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Path resolve = site.getOut().resolve(name);

        if (Files.notExists(resolve)) {
            if (Files.notExists(resolve.getParent())) {
                try {
                    Files.createDirectories(resolve.getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try (InputStream inputStream = url.openStream();
             OutputStream outputStream = Files.newOutputStream(resolve);) {

            byte[] data = new byte[1024];
            int number;
            while ((number = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, number);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderCollapsible() throws IOException, TemplateException {
        for (Library lib : site.getCollapsible()) {

            // 指定要生成的目录
            List<Article> collect = getArticles(site, lib, getAllFile());

            // 渲染
            // 指定一个 8 ，一般可能不会超过 8，默认为16
            Map<String, List<Article>> map = new HashMap<>(8);
            for (Article article : collect) {
                int start = article.getUrl().indexOf("\\") + 1;
                if (start == -1) {
                    throw new RuntimeException("start is -1 in " + article);
                }
                int end = article.getUrl().indexOf("\\", start);
                if (end > -1) {
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
                       .sorted()
                       .collect(Collectors.toList());
            String renderContent = FileUtil.render(new CollapsibleTemplate(site, lib.getName(), archiveIndices));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());
            LOGGER.info(resolve.toFile().getAbsolutePath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void renderList() throws IOException, TemplateException {
        for (Library lib : site.getList()) {
            List<Article> collect = getArticles(site, lib, getAllFile());

            Collections.sort(collect);

            // 渲染
            String renderContent = FileUtil.render(new ListTemplate(site, lib.getName(), collect));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());

            LOGGER.info(resolve.toFile().getAbsolutePath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static List<Article> getArticles(Site render, Library lib, List<String> allFile) {
        // 指定要生成的目录
        List<String> filterList =
                lib.getDir()
                   .stream()
                   .map(s -> render.getOut().toAbsolutePath().resolve(Paths.get(s).toFile().getName()).toString())
                   .collect(Collectors.toList());
        // 对所有已渲染的文件进行过滤
        List<Article> collect = new ArrayList<>();
        for (String file : allFile) {
            for (String filter : filterList) {
                if (file.startsWith(filter)) {
                    collect.add(new Article(getRelativizePath(render, file)));
                    break;
                }
            }
        }
        return collect;
    }

    private static Path getRelativizePath(Site render, String renderFilePath) {
        return render.getOut().toAbsolutePath().relativize(Paths.get(renderFilePath));
    }

    private static Path resolvePath(Site render, String resolvePath) {
        return render.getOut().resolve(resolvePath);
    }

    private void renderMd(Path path) {
        try {
            renderMd(path.toFile(), path.getParent());
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderMd(File file, Path root) throws IOException, TemplateException {
        File[] subFiles = file.listFiles();

        if (subFiles == null) {
            return;
        }

        for (File subFile : subFiles) {
            if (subFile.isDirectory()) {
                LOGGER.info(StringUtil.format("into dir: '{}'", subFile.getAbsolutePath()));

                renderMd(subFile, root);
            } else {
                if (!subFile.getName().endsWith(".md")) {
                    return;
                }

                LOGGER.info(StringUtil.format("  render md: '{}'", subFile.getAbsolutePath()));

                String outPath = FileUtil.generatedOutPath(root, subFile.toPath(), site.getOut()) + ".html";
                Path resolve = site.getOut().resolve(outPath);
                if (Files.notExists(resolve)) {
                    if (Files.notExists(resolve.getParent())) {
                        Files.createDirectories(resolve.getParent());
                    }
                    Files.createFile(resolve);
                }

                String renderContent = FileUtil.render(new MarkdownTemplate(site, subFile));
                Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

                allFile.add(outPath);
                LOGGER.info(StringUtil.format("    generated: '{}'", outPath));
            }
        }
    }

    private void deleteAllFiles(Path out) {
        FileUtil.deleteAllFiles(out.toFile());
    }

    private void copyComplexPath(List<File> stylePath, Path output) throws IOException {
        for (File path : stylePath) {
            if (path.isDirectory()) {
                FileUtil.copyFolder(path, output.resolve(path.getName()));
            } else if (path.exists()) {
                com.google.common.io.Files.copy(path, new File(output.toFile().getAbsolutePath() + File.separator + path.getName()));
            } else {
                LOGGER.warning(StringUtil.format("'{}' not copy, because it is not directory or no exist!", path));
            }
        }
    }

    public List<String> getAllFile() {
        return allFile;
    }
}
