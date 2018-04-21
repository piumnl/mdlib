package cn.piumnl.mdlib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.LogManager;

import cn.piumnl.mdlib.entity.MdlibProperties;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.util.RefelectUtil;
import cn.piumnl.mdlib.util.ResourceUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-24.
 */
public class Application {

    public static void main(String[] args) throws IOException {
        initLogger();
        processor();
    }

    private static void processor() throws IOException {
        // 读取配置文件 application.properties
        Path path = Paths.get("application.properties");
        Properties properties = ResourceUtil.loadProperties("/application.properties");
        if (Files.exists(path)) {
            properties = new Properties(properties);
            properties.load(Files.newInputStream(path));
        }

        Site site = new Site(RefelectUtil.inject(properties, MdlibProperties.class));

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

