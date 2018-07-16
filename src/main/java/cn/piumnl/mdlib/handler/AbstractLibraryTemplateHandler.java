package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.md.MarkdownTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public abstract class AbstractLibraryTemplateHandler implements Handler {

     void updateFile(File rootPath, Map<String, Article> articles) throws IOException {
        if (rootPath.isDirectory()) {
            File[] files = rootPath.listFiles((dir, name) -> dir.isDirectory() || (dir.isFile() && name.endsWith(".md")));
            if (files != null) {
                for (File file : files) {
                    updateFile(file, articles);
                }
            }
        } else if (rootPath.isFile()) {
            for (Map.Entry<String, Article> entry : articles.entrySet()) {
                if (rootPath.getAbsolutePath().endsWith(entry.getKey().replaceAll(".html", ".md"))) {
                    if (rootPath.lastModified() > entry.getValue().getUpdateTime()) {
                        MarkdownTemplate template = new MarkdownTemplate(Site.getInstance(), rootPath);
                        String render = FileUtil.render(template);
                        Path writeFile = Site.getInstance().getOut().resolve(entry.getKey());
                        Files.deleteIfExists(writeFile);
                        FileUtil.createFile(writeFile);
                        Files.write(writeFile, render.getBytes(StandardCharsets.UTF_8));
                        entry.getValue().setUpdateTime(rootPath.lastModified());
                        LoggerUtil.MDLIB_LOGGER.info("重新渲染 md 文件：{}", rootPath.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * 获取目录下所有的 md 文件并渲染，然后返回它们的输出目录
     * @param out 输出目录的根目录
     * @param lib md 文件所在源目录
     * @return md 文件渲染后的输出地址集合
     */
    List<Article> getArticles(Path out, String lib) throws IOException {
        // 指定要生成的目录
        String libDirName = Paths.get(lib).toFile().getName();
        Path libOutPath = out.toAbsolutePath().resolve(libDirName).normalize();
        if (Files.exists(libOutPath)) {
            LoggerUtil.MDLIB_LOGGER.error("目录 {} 已存在，是否多个库目录名一致，请检查", libOutPath.toString());
            throw new RuntimeException(StringUtil.format("目录 {} 已存在，是否多个库目录名一致，请检查",
                    libOutPath.toString()));
        } else {
            // 对所有已渲染的文件进行过滤
            List<Article> collect = new ArrayList<>();
            File originFile = new File(lib);
            if (originFile.isDirectory()) {
                findDirectoryFile(originFile, collect, originFile.getName());
            } else {
                addMarkdownFile(collect, originFile, "");
            }
            return collect;
        }
    }

    private void findDirectoryFile(File originFile, List<Article> collect, String relativeFileName)
            throws IOException {
        File[] files = originFile.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findDirectoryFile(file, collect, relativeFileName + "/" + file.getName());
            } else if (file.isFile()) {
                addMarkdownFile(collect, file, relativeFileName);
            }
        }
    }

    /**
     * 判断如果是 Markdown 文件就进行渲染
     * @param collect Markdown 文件输出目录集合
     * @param originFile markdown 文件
     * @param parentFile 父级目录，比如 Java 目录下 Framework 目录下 Spring.md 文件，此处值应该是 Java/Framework
     * @throws IOException IO 错误
     */
    private void addMarkdownFile(List<Article> collect, File originFile, String parentFile) throws IOException {
        if (originFile.getName().endsWith(".md")) {
            collect.add(doMarkdown(originFile, parentFile, originFile.getName()));
        }
    }

    /**
     * 渲染 md
     * @param originFile Markdown 原文件
     * @param parentFileName 输出目录
     * @param name Markdown 文件名
     * @return 文件名加地址
     * @throws IOException IO 错误
     */
    private Article doMarkdown(File originFile, String parentFileName, String name)
            throws IOException {
        // 记录日志
        Path out = Site.getInstance().getOut();
        LoggerUtil.PROCESSOR_LOGGER.info("  渲染md文件： '{}'", Paths.get(originFile.getAbsolutePath()).normalize());

        // 获取输出路径
        String fileNoExtName = name.substring(0, name.length() - 3) + ".html";
        Path outPath = out.resolve(parentFileName).resolve(fileNoExtName);
        FileUtil.createFile(outPath);

        // 渲染并写入文件
        MarkdownTemplate template = new MarkdownTemplate(Site.getInstance(), originFile);
        String render = FileUtil.render(template);
        Files.write(outPath, render.getBytes(StandardCharsets.UTF_8));

        // 记录日志
        LoggerUtil.PROCESSOR_LOGGER.info("          => '{}'", outPath.normalize());
        return new Article(Paths.get(parentFileName, fileNoExtName),
                template.getName(),
                originFile.lastModified());
    }
}
