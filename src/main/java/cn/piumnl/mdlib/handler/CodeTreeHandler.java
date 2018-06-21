package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.piumnl.mdlib.entity.CodeTree;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.fragment.CodeTemplate;
import cn.piumnl.mdlib.template.fragment.FragmentTemplate;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class CodeTreeHandler implements Handler {

    private static final String CODE_FILE_NAME = "代码库.html";

    @Override
    public void process(Site site) throws IOException {
        String codeTreeJSON = generatedCode(site.getCodePath());
        if (codeTreeJSON == null) {
            return;
        }
        LoggerUtil.PROCESSOR_LOGGER.info(codeTreeJSON);
        File file = new File(site.getCodePath());
        FileUtil.copyFolder(file, site.getOut().resolve(file.getName()), (source, target) -> {
            List<String> strings = Files.readAllLines(source.toPath(), StandardCharsets.UTF_8);
            String render = FileUtil.render(new FragmentTemplate(site, source.getName(), strings.stream().map(s -> s + "\n").reduce((s, s2) -> s + s2).orElse("")));
            String name = target.getName();
            name = name.substring(0, name.indexOf("."));
            Path resolve = target.toPath().getParent().resolve(name + ".html").toAbsolutePath().normalize();
            Files.write(resolve, render.getBytes(StandardCharsets.UTF_8));
            LoggerUtil.PROCESSOR_LOGGER.info(StringUtil.format("copy {} to {}", source, resolve));
        });

        Path codeHtml = site.getOut().resolve(CODE_FILE_NAME);
        if (Files.exists(codeHtml)) {
            Files.delete(codeHtml);
            LoggerUtil.PROCESSOR_LOGGER.warning(StringUtil.format("在 {} 目录下存在 {} 文件，正在删除中！", site.getOut(), CODE_FILE_NAME));
        }
        Files.createFile(codeHtml);
        String codeHtmlContent = FileUtil.render(new CodeTemplate(site, codeTreeJSON));
        Files.write(codeHtml, codeHtmlContent.getBytes(StandardCharsets.UTF_8));
    }

    private String generatedCode(String codePath) {
        Path codeDir = Paths.get(codePath);
        if (Files.notExists(codeDir)) {
            LoggerUtil.PROCESSOR_LOGGER.warning(StringUtil.format("lib.code '{}' 目录不存在！", codePath));
            return null;
        }

        if (!Files.isDirectory(codeDir)) {
            LoggerUtil.PROCESSOR_LOGGER.warning(StringUtil.format("lib.code '{}' 必须为目录，不允许为其他类型的文件！", codePath));
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
}
