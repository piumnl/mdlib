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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cn.piumnl.mdlib.entity.MdlibProperties;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.handler.Handler;
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

    /**
     * 资源基路径
     */
    public static String outPath;

    public static void main(String[] args) throws Exception {
        initLogger();
        Site site = processor();
        outPath = site.getOut().toAbsolutePath().toString();

        parseArgs(args, site);
    }

    private static void parseArgs(String[] args, Site site) throws IOException {
        Options options = new Options();

        //短选项，长选项，选项后是否有参数，描述
        Option runOpt = new Option("r", "run", false, "是否运行静态服务器");
        Option flushOpt = new Option("f", "refresh", true, "是否实时刷新 md 文件，刷新的时间间隔最小为 1 秒");
        Option portOpt = new Option("p", "port", true, "静态服务器所占端口");

        // 是否必须
        runOpt.setRequired(false);
        flushOpt.setRequired(false);
        portOpt.setRequired(false);

        options.addOption(runOpt);
        options.addOption(flushOpt);
        options.addOption(portOpt);

        try {
            // Posix 风格
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);

            // 标准HTTP端口
            int port = 20000;

            if (commandLine.hasOption(portOpt.getOpt())) {
                port = Integer.parseInt(commandLine.getOptionValue(portOpt.getOpt()));
            }

            if (commandLine.hasOption(flushOpt.getOpt())) {
                long sleepTime = Long.parseLong(commandLine.getOptionValue(flushOpt.getOpt()));
                final long time = sleepTime < 1000 ? 1000 : sleepTime;
                LoggerUtil.MDLIB_LOGGER.info("定时刷新时间为 {}", time);
                new Thread(() -> {
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
                }).start();
            }

            if (commandLine.hasOption(runOpt.getOpt())) {
                ServerSocket server = new ServerSocket(port);
                LoggerUtil.SERVER_LOGGER.info(StringUtil.format("静态资源服务器正在运行，端口为 {0}， 完整地址： http://localhost:{0}/", port));

                Socket client;
                while (true) {
                    client = server.accept();
                    new Thread(new ServerContext(client)).start();
                }
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Site processor() throws Exception {
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

    private static void initLogger() {
        InputStream stream = Application.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            LoggerUtil.MDLIB_LOGGER.error(e.getMessage());
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

