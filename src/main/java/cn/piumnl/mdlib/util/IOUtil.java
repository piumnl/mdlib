package cn.piumnl.mdlib.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-11.
 */
public interface IOUtil {

    /**
     *
     * @param output
     * @return
     */
    static OutputStreamWriter wrapperOut(OutputStream output) {
        return new OutputStreamWriter(output, StandardCharsets.UTF_8);
    }

    /**
     *
     * @param inputStream
     * @return
     */
    static InputStreamReader wrapperIn(InputStream inputStream) {
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }
}
