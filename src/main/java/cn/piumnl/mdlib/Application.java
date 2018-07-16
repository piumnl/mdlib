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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import cn.piumnl.mdlib.prop.MdlibProperties;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.handler.Handler;
import cn.piumnl.mdlib.prop.Args;
import cn.piumnl.mdlib.server.ServerContext;
import cn.piumnl.mdlib.util.FileUtil;
import cn.piumnl.mdlib.util.LoggerUtil;
import cn.piumnl.mdlib.util.RefelectUtil;
import cn.piumnl.mdlib.util.ResourceUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-24.
 */
public class Application {

    public static void main(String[] args) throws Exception {
        initLogger();
        run(args);
    }

    private static void initLogger() {
        InputStream stream = Application.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            LoggerUtil.MDLIB_LOGGER.error(e.getMessage());
        }
    }

    private static void run(String[] arguments) throws Exception {
        Args args = new Args(arguments);
        Site init = init();
        executedArgs(args, init);
    }

    private static Site init() throws Exception {
        // 读取配置文件 application.properties
        Path path = Paths.get("application.properties");
        Properties properties = ResourceUtil.loadProperties("/application.properties");
        if (Files.exists(path)) {
            properties = new Properties(properties);
            properties.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        }

        Site site = new Site(RefelectUtil.inject(properties, MdlibProperties.class));
        initProcessor(site);

        return site;
    }

    private static void executedArgs(Args args, Site site) throws IOException {
        if (args.isRun()) {
            ServerSocket server = new ServerSocket(args.getPort());
            LoggerUtil.SERVER_LOGGER.info(StringUtil.format("静态资源服务器正在运行，端口为 {0}， 完整地址： http://localhost:{0}/", args.getPort()));


            new Thread(() -> {
                Socket client;
                try {
                    while (true) {
                        client = server.accept();
                        new Thread(new ServerContext(client, site.getOut())).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        if (args.isRefresh()) {
            final long time = args.getRefreshTime() < 1000 ? 1000 : args.getRefreshTime();
            LoggerUtil.MDLIB_LOGGER.info("定时刷新时间为 {}", time);
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> timedRefresh(site, time));
        }
    }

    private static void timedRefresh(Site site, long time) {
        while (true) {
            try {
                try {
                    Thread.sleep(time);
                    LoggerUtil.MDLIB_LOGGER.info("Refreshing...");
                    Handler.refreshHandler(site);
                    LoggerUtil.MDLIB_LOGGER.info("Refresh finished!");
                } catch (InterruptedException e) {
                    LoggerUtil.MDLIB_LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    LoggerUtil.MDLIB_LOGGER.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                // do nothing
            }
        }
    }

    private static void initProcessor(Site site) throws Exception {
        // 删除输出目录所有文件
        FileUtil.deleteDirectory(site.getOut().toFile());
        if (site.isDefaultStaticPath()) {
            if (FileUtil.isJar()) {
                ResourceUtil.copyJarResource(site.getOut(), name -> name.startsWith("static/") && !name.endsWith("/"));
            } else {
                site.getStaticPath().add(FileUtil.classPath("static"));
            }
        } else {
            if (FileUtil.isJar()) {
                ResourceUtil.copyJarResource(site.getOut(), name -> StringUtil.equals(name, Site.STATIC_ML_ICO));
            } else {
                site.getStaticPath().add(FileUtil.classPath(Site.STATIC_ML_ICO));
            }
        }

        Handler.initHandler(site);
    }
}
