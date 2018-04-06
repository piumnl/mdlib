package cn.piumnl.mdlib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.LogManager;

import freemarker.template.TemplateException;

import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.ResourceUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-24.
 */
public class Application {

    static {
        try {
            // 如果是在 jar 中需要手动创建一个文件系统，但在非 jar 中不需要创建
            if (FileUtil.isJar()) {
                URI uri = Application.class.getResource("").toURI();
                FileSystem fileSystem = FileSystems.newFileSystem(uri, new HashMap<>());;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {
        initLogger();
        processor();
    }

    private static void processor() throws IOException, TemplateException {
        // 读取配置文件 application.properties
        Path path = Paths.get("application.properties");
        Properties properties = ResourceUtil.loadProperties("/application.properties");
        if (Files.exists(path)) {
            properties = new Properties(properties);
            properties.load(Files.newInputStream(path));
        }

        Site site = new Site(properties);

        Processor processor = new Processor(site);
        processor.processor();
    }

    private static void initLogger() {
        InputStream stream = Application.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

