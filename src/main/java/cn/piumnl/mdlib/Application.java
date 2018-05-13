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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

        parseArgs(args);
    }

    private static void parseArgs(String[] args) throws IOException {
        Options options = new Options();

        //短选项，长选项，选项后是否有参数，描述
        Option runOpt = new Option("r", "run", false, "是否运行静态服务器");
        Option flushOpt = new Option("f", "flush", false, "是否实时刷新 md 文件");
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
                // Executors.newCachedThreadPool()
                // BasicThreadFactory build =
                //         new BasicThreadFactory.Builder()
                //                 .namingPattern("flush-md-%d")
                //                 .daemon(true)
                //                 .build();
                // ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1, build);
                // service.
            }

            if (commandLine.hasOption(runOpt.getOpt())) {
                Logger logger = Logger.getLogger(ServerContext.class.getName());
                ServerSocket server = new ServerSocket(port);
                logger.info(StringUtil.format("静态资源服务器正在运行，端口为 {0}， 完整地址： http://localhost:{0}/", port));

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
            Logger.getLogger(Application.class.getName()).severe(e.getMessage());
        }
    }
}

