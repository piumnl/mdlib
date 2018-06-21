package cn.piumnl.mdlib.util;

import java.util.logging.Logger;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public interface LoggerUtil {

    Logger MDLIB_LOGGER = Logger.getLogger("cn.piumnl.mdlib");

    Logger PROCESSOR_LOGGER = Logger.getLogger("cn.piumnl.mdlib.processor");

    Logger SERVER_LOGGER = Logger.getLogger("cn.piumnl.mdlib.server");

}
