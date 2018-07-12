package cn.piumnl.mdlib.util;

import cn.piumnl.mdlib.annotation.Property;
import cn.piumnl.mdlib.entity.Library;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-19.
 */
public class RefelectUtil {

    private RefelectUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 通过 Properties 对象实例化一个类对象，该类中包含 {@link Property} 注解的字段将会被初始化为 Properties
     * 对象中的一个 key 对应的值。
     * @param properties properties 文件中的键值对
     * @param tClass 需要实例化的类的 Class 对象
     * @param <T> 需要实例化的类
     * @return 实例化后的对象
     */
    public static <T> T inject(Properties properties, Class<T> tClass) {
        try {
            T t = tClass.newInstance();

            for (Field field : tClass.getDeclaredFields()) {
                field.setAccessible(true);

                Property annotation = field.getAnnotation(Property.class);
                if (annotation == null) {
                    break;
                }

                if (annotation.isPrefix()) {
                    List<Library> map = doPrefixKey(properties, annotation.value(), field);
                    setFieldValue(field, t, map, annotation.separator());
                } else {
                    String value = properties.getProperty(annotation.value(), annotation.defaultValue());
                    setFieldValue(field, t, value, annotation.separator());
                }
            }

            return t;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Library> doPrefixKey(Properties properties,
                                                         String annotation,
                                                         Field field) {
        // 此字段的类型必须为 Map 的子类
        // if (!Library.class.isAssignableFrom(field.getType())) {
        //     throw new RuntimeException(StringUtil.format("字段 '{}' 的类型应该是 Library", field.getName()));
        // }

        List<Library> map = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            // 避免出现意外中的值
            if (canInjectValue(properties, annotation, key)) {
                int indexOf = annotation.length() + ".".length();
                map.add(new Library(key.substring(indexOf), properties.getProperty(key, "")));
            }
        }

        return map;
    }

    /**
     * 是否可以注入值到 Map 中？注解必须为 key 的前缀，并且 value 中不能为空
     * @param properties 配置文件对象
     * @param annotation 注解值
     * @param key key 值
     * @return 如果可以返回 true，否则返回 false
     */
    private static boolean canInjectValue(Properties properties, String annotation, String key) {
        // 加 . 是为了避免极端情况，比如 lib.module.xxx 与 lib.moduleXxx。
        return key.startsWith(annotation + ".") && StringUtil.isNotEmpty(properties.getProperty(key));
    }

    /**
     * 将 value 值取出并转换为 {@link List<String>} 对象
     * @param properties 配置文件对象
     * @param key key 值
     * @return value 转换为 {@link List<String>} 后的值
     */
    // private static List<String> getValueList(Properties properties, String key) {
    //     return Arrays.asList(properties.getProperty(key, "").split(","));
    // }

    /**
     * 为字段设置值，支持基本类型 + List<String> + Set<String> + String[]
     * @param field 字段对象
     * @param obj 具体字段对应的对象
     * @param value 需要设置的值
     * @param separator 如果是集合或数组的话对 value 进行分割的分割符
     */
    private static void setFieldValue(Field field, Object obj, Object value, String separator) {
        try {
            field.setAccessible(true);

            if (field.getType() == int.class || field.getType() == Integer.class) {
                field.setInt(obj, Integer.parseInt(value.toString()));
            } else if (field.getType() == short.class || field.getType() == Short.class) {
                field.setShort(obj, Short.parseShort(value.toString()));
            } else if (field.getType() == byte.class || field.getType() == Byte.class) {
                field.setByte(obj, Byte.parseByte(value.toString()));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.setBoolean(obj, Boolean.parseBoolean(value.toString()));
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                field.setLong(obj, Long.parseLong(value.toString()));
            } else if (field.getType() == float.class || field.getType() == Float.class) {
                field.setFloat(obj, Float.parseFloat(value.toString()));
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                field.setDouble(obj, Double.parseDouble(value.toString()));
            } else if (field.getType() == char.class || field.getType() == Character.class) {
                field.setChar(obj, value.toString().charAt(0));
            } else if (field.getType().isArray()) {
                field.set(obj, value.toString().split(separator));
            } else if (List.class.isAssignableFrom(field.getType())) {
                field.set(obj, value);
            } else if (Set.class.isAssignableFrom(field.getType())) {
                field.set(obj, value);
            } else {
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
