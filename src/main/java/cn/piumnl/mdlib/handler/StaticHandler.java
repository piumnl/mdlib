package cn.piumnl.mdlib.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * 静态资源处理器
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public class StaticHandler implements Handler {

    @Override
    public void process(Site site) throws IOException {
        List<File> staticPath = site.getStaticPath();
        Path output = site.getOut();

        for (File path : staticPath) {
            if (path.isDirectory()) {
                FileUtil.copyFolder(path, output.resolve(path.getName()));
            } else if (path.exists()) {
                FileUtil.copy(path, new File(output.toFile().getAbsolutePath() + File.separator + path.getName()));
            } else {
                LoggerUtil.PROCESSOR_LOGGER.warning(StringUtil.format("'{}' not copy, because it is not directory or no exist!", path));
            }
        }
    }
}
