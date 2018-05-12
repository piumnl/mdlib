package cn.piumnl.mdlib.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class ResourceUtil {

    private ResourceUtil() {
    }

    public static Properties loadProperties(String name) throws IOException {
        try (Reader stream = new InputStreamReader(ResourceUtil.class.getResourceAsStream(name), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }

    public static void copyJarResource(Path target, Predicate<String> predicate) throws IOException {
        JarFile jarFile = getCurrentJarFile();
        String jarURL = "jar:file:/" + jarFile.getName().replaceAll("\\\\", "/") + "!/";
        jarFile.stream()
               .map(ZipEntry::getName)
               .filter(predicate)
               .forEach(name -> copy(jarURL, name, target));
    }

    private static void copy(String jarFile, String name, Path target) {
        URL url;
        try {
            url = new URI(jarFile + name).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Path resolve;
        try {
            resolve = target.resolve(name);
            FileUtil.createFile(resolve);
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    public static JarFile getCurrentJarFile() throws IOException {
        try {
            JarURLConnection connection =
                    (JarURLConnection) ResourceUtil.class.getResource("").toURI().toURL().openConnection();
            return connection.getJarFile();
        } catch (URISyntaxException | MalformedURLException e) {
            // uri 是系统提供的，不存在 uri 语法问题。
            throw new RuntimeException(e);
        }
    }
}
