package cn.piumnl.mdlib;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import cn.piumnl.mdlib.entity.ArchiveIndex;
import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.CodeTree;
import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.CodeTemplate;
import cn.piumnl.mdlib.template.CollapsibleTemplate;
import cn.piumnl.mdlib.template.FragmentTemplate;
import cn.piumnl.mdlib.template.ListTemplate;
import cn.piumnl.mdlib.template.MarkdownTemplate;
import cn.piumnl.mdlib.template.SingleTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.ResourceUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class Processor {

    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());

    private static final String CODE_FILE_NAME = "代码库.html";

    private Site site;

    private List<String> allFile = new ArrayList<>();

    public Processor(Site site) {
        this.site = site;
    }

    public void processor() throws IOException {
        // 删除输出目录所有文件
        deleteAllFiles(site.getOut());
        if (site.isDefaultStaticPath()) {
            if (FileUtil.isJar()) {
                ResourceUtil.copyJarResource(site.getOut(), name -> name.startsWith("static/") && !name.endsWith("/"));
            } else {
                site.getStaticPath().add(FileUtil.classPath("static"));
            }
        } else {
            if (FileUtil.isJar()) {
                ResourceUtil.copyJarResource(site.getOut(), name -> StringUtil.equals(name, Site.STATIC_ML_ICO));
            } else {
                site.getStaticPath().add(FileUtil.classPath(Site.STATIC_ML_ICO));
            }
        }

        // 复制静态资源
        List<File> staticPath = site.getStaticPath();

        copyComplexPath(staticPath, site.getOut());
        // 渲染 md
        site.getLibraries()
            .stream()
            .flatMap(library -> library.getDir().stream())
            .distinct()
            .forEach(p -> renderMd(Paths.get(p).toAbsolutePath()));

        // -----------------------------------------------

        // List 配置
        renderList();
        // Collapsible 配置
        renderCollapsible();
        // Single 配置
        renderSingle();

        // -----------------------------------------------

        // code 处理
        String codeTreeJSON = generatedCode(site.getCodePath());
        if (codeTreeJSON == null) {
            return;
        }
        LOGGER.info(codeTreeJSON);
        File file = new File(site.getCodePath());
        FileUtil.copyFolder(file, site.getOut().resolve(file.getName()), (source, target) -> {
            List<String> strings = Files.readAllLines(source.toPath(), StandardCharsets.UTF_8);
            String render = FileUtil.render(new FragmentTemplate(site, source.getName(), strings.stream().map(s -> s + "\n").reduce((s, s2) -> s + s2).orElse("")));
            String name = target.getName();
            name = name.substring(0, name.indexOf("."));
            Path resolve = target.toPath().getParent().resolve(name + ".html").toAbsolutePath().normalize();
            Files.write(resolve, render.getBytes(StandardCharsets.UTF_8));
            LOGGER.info(StringUtil.format("copy {} to {}", source, resolve));
        });

        Path codeHtml = site.getOut().resolve(CODE_FILE_NAME);
        if (Files.exists(codeHtml)) {
            Files.delete(codeHtml);
            LOGGER.warning(StringUtil.format("在 {} 目录下存在 {} 文件，正在删除中！", site.getOut(), CODE_FILE_NAME));
        }
        Files.createFile(codeHtml);
        String codeHtmlContent = FileUtil.render(new CodeTemplate(site, codeTreeJSON));
        Files.write(codeHtml, codeHtmlContent.getBytes(StandardCharsets.UTF_8));
    }

    private String generatedCode(String codePath) {
        Path codeDir = Paths.get(codePath);
        if (Files.notExists(codeDir)) {
            LOGGER.warning(StringUtil.format("lib.code '{}' 目录不存在！", codePath));
            return null;
        }

        if (!Files.isDirectory(codeDir)) {
            LOGGER.warning(StringUtil.format("lib.code '{}' 必须为目录，不允许为其他类型的文件！", codePath));
            return null;
        }

        File dir = codeDir.toFile();
        CodeTree root = new CodeTree(dir.getName());
        tree(root, dir);

        return JSON.toJSONString(root);
    }

    private void tree(CodeTree parent, File dir) {
        if (dir == null) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }

                CodeTree tree = new CodeTree(file.getName(), parent);
                parent.getChildren().add(tree);

                if (file.isDirectory()) {
                    tree(tree, file);
                }
            }
        }
    }

    private void renderSingle() throws IOException {
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
            LOGGER.info(resolve.toAbsolutePath().normalize().toString());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void renderCollapsible() throws IOException {
        for (Library lib : site.getCollapsible()) {

            // 指定要生成的目录
            List<Article> collect = getArticles(site, lib, allFile);

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
            LOGGER.info(resolve.toAbsolutePath().normalize().toFile().getPath());
            FileUtil.createFile(resolve);
            Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

            // 生成 index.html
            Path path = resolve.resolveSibling("index.html");
            if (Files.notExists(path)) {
                Files.write(path, renderContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void renderList() throws IOException {
        for (Library lib : site.getList()) {
            List<Article> collect = getArticles(site, lib, allFile);

            Collections.sort(collect);

            // 渲染
            String renderContent = FileUtil.render(new ListTemplate(site, lib.getName(), collect));

            // 输出
            Path resolve = resolvePath(site, lib.getUrl());

            LOGGER.info(resolve.toAbsolutePath().normalize().toFile().getPath());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderMd(File file, Path root) throws IOException {
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

                LOGGER.info(StringUtil.format("  render md: '{}'", Paths.get(subFile.getAbsolutePath()).normalize()));

                String outPath = FileUtil.generatedOutPath(root, subFile.toPath(), site.getOut()) + ".html";
                Path resolve = site.getOut().resolve(outPath);
                FileUtil.createFile(resolve);

                String renderContent = FileUtil.render(new MarkdownTemplate(site, subFile));
                Files.write(resolve, renderContent.getBytes(StandardCharsets.UTF_8));

                allFile.add(outPath);
                LOGGER.info(StringUtil.format("          => '{}'", Paths.get(outPath).normalize()));
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
}
