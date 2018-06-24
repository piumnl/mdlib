package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * 静态资源处理器
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class StaticHandler implements Handler {

    private static final StaticHandler HANDLER = new StaticHandler();

    private Map<String, Long> fileInfo;

    private StaticHandler() {
    }

    public static StaticHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void refresh(Site site) throws Exception {
        List<File> staticPath = site.getStaticPath();
        Path outDir = site.getOut();

        for (File file : staticPath) {
            if (file.isDirectory()) {
                refreshDirectory(file, outDir.resolve(file.getName()));
            } else if (file.exists()) {
                refreshFile(file, outDir.toFile());
            } else {
                LoggerUtil.PROCESSOR_LOGGER.warning("'{}' not copy, because it is not directory or no exist!", file);
            }
        }
    }

    @Override
    public void process(Site site) throws IOException {
        List<File> staticPath = site.getStaticPath();
        Path outDir = site.getOut();

        // 数目指定多一点
        fileInfo = new HashMap<>(72);
        for (File file : staticPath) {
            if (file.isDirectory()) {
                copyDirectory(file, outDir.resolve(file.getName()));
            } else if (file.exists()) {
                copyFile(file, new File(outDir.toFile().getAbsolutePath()));
            } else {
                LoggerUtil.PROCESSOR_LOGGER.warning("'{}' not copy, because it is not directory or no exist!", file);
            }
        }
    }

    private void copyDirectory(File source, Path target) throws IOException {
        FileUtil.copyDirectory(source, target, this::copyFile);
    }

    private void copyFile(File source, File target) throws IOException {
        if (target.exists() && !target.delete()) {
            LoggerUtil.PROCESSOR_LOGGER.warning("删除文件 {} 失败", target.getName());
        }

        copy(source, target);
    }

    private void refreshDirectory(File sourceFile, Path output) throws IOException {
        FileUtil.copyDirectory(sourceFile, output, this::refreshFile, false);
    }

    private void refreshFile(File source, File target) throws IOException {
        Long lastModified = fileInfo.get(source.getAbsolutePath());
        if (lastModified == null || lastModified < source.lastModified()) {
            copy(source, target.toPath().toAbsolutePath().normalize().toFile());
        }
    }

    private void copy(File source, File target) throws IOException {
        LoggerUtil.PROCESSOR_LOGGER.info("渲染文件：{}", source.getAbsolutePath());
        fileInfo.put(source.getAbsolutePath(), source.lastModified());
        FileUtil.copy(source, target);
    }
}
