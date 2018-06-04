package cn.piumnl.mdlib.util;

import java.io.IOException;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-04.
 */
@FunctionalInterface
public interface FileFuncational<T> {

    void accept(T source, T target) throws IOException;
}
