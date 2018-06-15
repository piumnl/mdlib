package cn.piumnl.mdlib.util;

import java.io.IOException;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-04.
 */
@FunctionalInterface
public interface FileFuncational<T> {

    /**
     * 对源和目标两个值进行操作
     *
     * @param source 源
     * @param target 目标
     * @throws IOException 可能存在 IO 读写
     */
    void accept(T source, T target) throws IOException;
}
