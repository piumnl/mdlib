package cn.piumnl.mdlib;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import cn.piumnl.mdlib.entity.MdlibProperties;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.server.ServerContext;
import cn.piumnl.mdlib.util.RefelectUtil;
import cn.piumnl.mdlib.util.ResourceUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-24.
 */
public class Application {

    /**
     * 资源基路径
     */
    public static String outPath;

    public static void main(String[] args) throws IOException {
        initLogger();
        Site processor = processor();
        outPath = processor.getOut().toAbsolutePath().toString();

        if (args.length > 0 && StringUtil.equals(args[0], "run")) {
            Logger logger = Logger.getLogger(ServerContext.class.getName());
            // 标准HTTP端口
            int port = 20012;
            ServerSocket server = new ServerSocket(port);
            logger.info("静态资源服务器正在运行，端口为 " + port);

            // 等待客户端的连接请求
            // 获取请求的资源路径
            // 读取文件返回

            Socket client;
            while (true) {
                client = server.accept();
                // ServerContext serverContext = ;
                // serverContext.getResponse().done();
                // Handler handler = new Handler(client, processor.getOut().toAbsolutePath().toString());
                new Thread(new ServerContext(client)).start();
            }
        }
    }

    private static Site processor() throws IOException {
        // 读取配置文件 application.properties
        Path path = Paths.get("application.properties");
        Properties properties = ResourceUtil.loadProperties("/application.properties");
        if (Files.exists(path)) {
            properties = new Properties(properties);
            properties.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        }

        Site site = new Site(RefelectUtil.inject(properties, MdlibProperties.class));

        Processor processor = new Processor(site);
        processor.processor();

        return site;
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

