package cn.piumnl.mdlib.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class ResourceUtil {

    public static Properties loadProperties(String name) throws IOException {
        InputStream stream = ResourceUtil.class.getResourceAsStream(name);
        if (stream == null) {
            throw new RuntimeException(StringUtil.format("Can't find properties file '{}'!", name));
        }

        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    public static Path applicationDir() {
        return Paths.get(System.getProperty("user.dir"));
    }
}
