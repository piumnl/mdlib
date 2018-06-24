package cn.piumnl.mdlib.util;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public interface LoggerUtil {

    Log MDLIB_LOGGER = new Log(Logger.getLogger("cn.piumnl.mdlib"));

    Log PROCESSOR_LOGGER = new Log(Logger.getLogger("cn.piumnl.mdlib.processor"));

    Log SERVER_LOGGER = new Log(Logger.getLogger("cn.piumnl.mdlib.server"));

    class Log {

        private Logger logger;

        public Log(Logger logger) {
            Objects.requireNonNull(logger);
            this.logger = logger;
        }

        public void trace(String msg, Object... data) {
            logger.finest(StringUtil.format(msg, data));
        }

        public void debug(String msg, Object... data) {
            logger.fine(StringUtil.format(msg, data));
        }

        public void info(String msg, Object... data) {
            logger.info(StringUtil.format(msg, data));
        }

        public void warning(String msg, Object... data) {
            logger.warning(StringUtil.format(msg, data));
        }

        public void error(String msg, Object... data) {
            logger.severe(StringUtil.format(msg, data));
        }
    }
}
