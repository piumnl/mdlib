package cn.piumnl.mdlib.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2017-02-14.
 */
public interface StringUtil {

    /**
     * 去除参数前后的空格字符
     *
     * @param in .
     * @return 去除了前后空格字符的参数值
     */
    static String trim(String in) {
        String out = in;

        if (out != null) {
            out = out.trim();

            if ("".equals(out)) {
                out = null;
            }
        }

        return out;
    }

    /**
     * 将对象信息使用 [ 和 ] 包含
     *
     * @param msg 对象
     * @return 如果对象信息存在，则调用toString() 并将结果用中括号包裹，否则返回空字符串
     */
    static String block(Object msg) {
        if (msg != null) {
            return  "[" + msg.toString() + "]";
        } else {
            return "[]";
        }
    }

    /**
     * 判断两个字符串是否相等
     * @param first 第一个字符串
     * @param two 第二个字符串
     * @return 如果两个字符串相等返回 true，否则返回 false
     */
    static boolean equals(String first, String two) {
        if (first != null && first.equals(two)) {
            return true;
        }

        return two != null && two.equals(first);
    }

    /**
     * 判断两个字符串在不区分大小写上是否相等
     * @param first 第一个字符串
     * @param two 第二个字符串
     * @return 如果两个字符串不区分大小写相等返回 true，否则返回 false
     */
    static boolean equalsIgnoreCase(String first, String two) {
        String f = get(first).toLowerCase();
        String t = get(two).toLowerCase();

        return f.equals(t);
    }

    /**
     * 包裹字符串
     * @param str 需要包裹的字符串
     * @return 如果字符串为 null，则返回空字符串；否则返回其本身
     */
    static String get(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 判断 字符序列 是否为空
     *
     * @param value 字符序列值
     * @return 如果字符序列为 @{code null} 或者 长度为0 返回 true，否则返回false
     */
    static boolean isEmpty(CharSequence value) {
        return value == null || value.length() == 0;
    }

    /**
     * 判断 字符序列 是否不为空
     *
     * @param value 字符序列值
     * @see StringUtil#isEmpty(CharSequence)
     * @return 如果字符序列为 @{code null} 或者 长度为 0 返回 false，否则返回 true
     */
    static boolean isNotEmpty(CharSequence value) {
        return !isEmpty(value);
    }

    /**
     * 如果为空则返回默认值
     *
     * @param value
     * @param defaultValue
     * @return
     */
    static String of(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    static int of(String value, int defaultValue) {
        return isEmpty(value) ? defaultValue : Integer.parseInt(value);
    }

    static boolean indexOf(String findStr, String[] matchStr) {
        for (String match : matchStr) {
            if (equals(findStr, match)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符串 str 是否在 origin 数组中
     *
     * @param str .
     * @param origin .
     * @return 如果存在返回 true，否则返回 false
     */
    static boolean contain(final String str, String[] origin) {
        return Arrays.stream(Optional.ofNullable(origin)
                                     .orElse(new String[0]))
                     .anyMatch(s -> equals(s, str));
    }

    /**
     * 格式化字符串，如下示例：
     * <code>
     *     a{0}b{1}c + 1, 2, 3 => a1b2c
     *     a{}b{}c + 1, 2, 3 => a1b2c
     * </code>
     *
     * @param pattern 需要格式化的字符串
     * @param arguments 参数
     * @return 格式化后的内容
     */
    static String format(String pattern, Object... arguments) {
        if (isEmpty(pattern)) {
            throw new RuntimeException("pattern 不能为空");
        }
        Objects.requireNonNull(arguments);

        int part = 0;
        boolean isStart = false;
        int index = 0;
        StringBuilder builder = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch == '{') {
                isStart = true;
                temp.append('{');
            } else if (ch == '}' && isStart) {
                if (temp.length() == 1) {
                    builder.append(arguments[part]);
                    part++;
                } else {
                    builder.append(arguments[index]);
                    index = 0;
                }
                isStart = false;
                temp = new StringBuilder();
            } else {
                if (isStart) {
                    temp.append(ch);
                    if (ch >= '0' && ch <= '9') {
                        index = index * 10 + (ch - '0');
                    } else {
                        isStart = false;
                        builder.append(temp);
                        temp = new StringBuilder();
                    }
                } else {
                    builder.append(ch);
                }
            }
        }

        if (temp.length() > 0) {
            builder.append(temp);
        }

        return builder.toString();
    }
}
