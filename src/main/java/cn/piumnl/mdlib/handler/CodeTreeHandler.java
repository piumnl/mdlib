package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final CodeTreeHandler HANDLER = new CodeTreeHandler();

    private Map<String, Long> fileInfo;

    private CodeTreeHandler() {
    }

    public static CodeTreeHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void refresh(Site site) throws Exception {
        File file = new File(site.getCodePath());
        List<Boolean> isModified = new ArrayList<>(1);
        FileUtil.copyDirectory(file, site.getOut().resolve(file.getName()), (source, target) -> {
            Long lastModified = fileInfo.get(source.getAbsolutePath());
            if (lastModified == null
                    || lastModified < source.lastModified()) {
                renderCodePage(site, source, target);
                LoggerUtil.PROCESSOR_LOGGER.debug("渲染代码页：{}", source.getAbsolutePath());
                isModified.add(true);
            }
        });

        if (isModified.size() == 1) {
            renderCodeIndexPage(site);
        }
    }

    @Override
    public void process(Site site) throws IOException {
        fileInfo = new HashMap<>();
        File file = new File(site.getCodePath());
        FileUtil.copyDirectory(file, site.getOut().resolve(file.getName()), (source, target) -> renderCodePage(site, source, target));

        renderCodeIndexPage(site);
    }

    /**
     * 渲染代码主页，即有 ztree 的页面
     * @param site 站点信息
     * @throws IOException
     */
    private void renderCodeIndexPage(Site site) throws IOException {
        String codeTreeJSON = generatedCode(site.getCodePath());
        if (codeTreeJSON == null) {
            return;
        }
        LoggerUtil.PROCESSOR_LOGGER.info(codeTreeJSON);

        Path codeHtml = site.getOut().resolve(CODE_FILE_NAME);
        if (Files.exists(codeHtml)) {
            Files.delete(codeHtml);
            LoggerUtil.PROCESSOR_LOGGER.warning("在 {} 目录下存在 {} 文件，正在删除中！", site.getOut().toAbsolutePath(), CODE_FILE_NAME);
        }
        Files.createFile(codeHtml);
        String codeHtmlContent = FileUtil.render(new CodeTemplate(site, codeTreeJSON));
        Files.write(codeHtml, codeHtmlContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 渲染代码页，即所有文件将会作为文本读取，并用 markdown 的代码块进行渲染
     * @param site 站点信息
     * @param source 源文件
     * @param target 目标文件
     * @return 渲染后的目标文件对象
     * @throws IOException
     */
    public void renderCodePage(Site site, File source, File target) throws IOException {
        if (target.exists() && target.delete()) {
            throw new RuntimeException(StringUtil.format("删除文件 {} 失败", target.getAbsoluteFile()));
        }

        List<String> strings = Files.readAllLines(source.toPath(), StandardCharsets.UTF_8);
        String render = FileUtil.render(new FragmentTemplate(site, source.getName(), strings.stream().map(s -> s + "\n").reduce((s, s2) -> s + s2).orElse("")));
        String name = target.getName();
        name = name.substring(0, name.indexOf("."));
        Path resolve = target.toPath().getParent().resolve(name + ".html").toAbsolutePath().normalize();
        Files.write(resolve, render.getBytes(StandardCharsets.UTF_8));
        fileInfo.put(source.getAbsolutePath(), source.lastModified());
        LoggerUtil.PROCESSOR_LOGGER.info("复制 {} 到 {}", source, resolve);
    }

    /**
     * 生成 ztree 格式的 JSON 字符串
     * @param codePath 目录树
     * @return ztree 格式的 JSON 字符串
     */
    private String generatedCode(String codePath) {
        Path codeDir = Paths.get(codePath);
        if (Files.notExists(codeDir)) {
            LoggerUtil.PROCESSOR_LOGGER.warning("lib.code '{}' 目录不存在！", codePath);
            return null;
        }

        if (!Files.isDirectory(codeDir)) {
            LoggerUtil.PROCESSOR_LOGGER.warning("lib.code '{}' 必须为目录，不允许为其他类型的文件！", codePath);
            return null;
        }

        File dir = codeDir.toFile();
        CodeTree root = new CodeTree(dir.getName());
        tree(root, dir);

        return JSON.toJSONString(root);
    }

    /**
     * 生成 {@link CodeTree} 对象
     * @param parent 父级的 CodeTree 对象
     * @param dir 要生成的 tree 所在的文件目录
     */
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
