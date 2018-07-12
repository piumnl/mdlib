package cn.piumnl.mdlib.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Path;

import cn.piumnl.mdlib.util.IOUtil;
import cn.piumnl.mdlib.util.LoggerUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-12.
 */
public class ServerRequest {

    private static final LoggerUtil.Log LOGGER = LoggerUtil.SERVER_LOGGER;

    private static final String INDEX_HTML = "/index.html";

    private InputStream inputStream;

    private String resourcePath;

    private String mime;

    private Path realPath;

    private ServerContext context;

    private Path rootPath;

    public ServerRequest(InputStream inputStream, ServerContext context, Path rootPath) {
        this.inputStream = inputStream;
        this.context = context;
        this.rootPath = rootPath.toAbsolutePath().normalize();
    }

    public void init() throws IOException {
        BufferedReader reader = new BufferedReader(IOUtil.wrapperIn(inputStream));
        String header = reader.readLine();

        LOGGER.trace(">>>: " + header);
        // 读取所有浏览器发送过来的请求参数头部的所有信息

        // 获得请求的资源的地址
        resourcePath = header.split(" ")[1];
        int endIndex = resourcePath.indexOf("?");
        if (endIndex != -1) {
            resourcePath = resourcePath.substring(0, endIndex);
        }
        LOGGER.trace("\tresource path:" + resourcePath);

        if ("/".equals(resourcePath)) {
            resourcePath = INDEX_HTML;
        }

        String suffix;
        String[] names = resourcePath.split("\\.");
        suffix = names[names.length - 1];
        mime = MIME.get(suffix);

        realPath = rootPath.resolve(URLDecoder.decode(resourcePath, "UTF-8").substring(1));
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getMime() {
        return mime;
    }

    public Path getRealPath() {
        return realPath;
    }
}
