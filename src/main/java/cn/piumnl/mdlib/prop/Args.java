package cn.piumnl.mdlib.prop;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 封装传入的参数
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-07-09.
 */
public class Args {

    private int port;

    private boolean refresh;

    private int refreshTime;

    private boolean run;

    private Args() {
        port = 20000;
        refresh = false;
        run = false;
    }

    public Args(String[] args) throws ParseException {
        this();
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

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(flushOpt.getOpt())) {
            refresh = true;
            refreshTime = Integer.parseInt(commandLine.getOptionValue(flushOpt.getOpt()));
            run = true;
        }

        if (commandLine.hasOption(portOpt.getOpt())) {
            port = Integer.parseInt(commandLine.getOptionValue(portOpt.getOpt()));
            run = true;
        }

        if (commandLine.hasOption(runOpt.getOpt())) {
            run = true;
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public boolean isRun() {
        return run;
    }

    @Override
    public String toString() {
        return "Args{" +
                "port=" + port +
                ", refresh=" + refresh +
                ", refreshTime=" + refreshTime +
                ", run=" + run +
                '}';
    }
}
