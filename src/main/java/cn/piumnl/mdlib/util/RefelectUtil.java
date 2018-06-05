package cn.piumnl.mdlib.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cn.piumnl.mdlib.annotation.Property;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-19.
 */
public interface RefelectUtil {

    Logger LOGGER = Logger.getLogger("cn.piumnl.mdlib");

    /**
     * 通过 Properties 对象实例化一个类对象，该类中包含 {@link Property} 注解的字段将会被初始化为 Properties
     * 对象中的一个 key 对应的值。
     * @param properties properties 文件中的键值对
     * @param tClass 需要实例化的类的 Class 对象
     * @param <T> 需要实例化的类
     * @return 实例化后的对象
     */
    static <T> T inject(Properties properties, Class<T> tClass) {
        try {
            Field[] declaredFields = tClass.getDeclaredFields();
            Property annotation;
            String value;
            T t = tClass.newInstance();

            for (Field field : declaredFields) {
                field.setAccessible(true);

                annotation = field.getAnnotation(Property.class);
                if (annotation != null) {
                    if (annotation.isPrefix()) {
                        if (Map.class.isAssignableFrom(field.getType())) {
                            Map<String, List<String>> map = new HashMap<>();
                            for (String key : properties.stringPropertyNames()) {
                                if (key.startsWith(annotation.value())) {
                                    // 存在 bug ，当没有值的情况
                                    int indexOf = key.indexOf(annotation.value()) + 1 + annotation.value().length();
                                    map.put(key.substring(indexOf), Arrays.asList(properties.getProperty(key, "").split(",")));
                                }
                            }
                            setFieldValue(field, t, map, annotation.separator());
                        } else {
                            throw new RuntimeException(StringUtil.format("字段 '{}' 的类型应该是Map", field.getName()));
                        }
                    } else {
                        value = properties.getProperty(annotation.value(), annotation.defaultValue());
                        setFieldValue(field, t, value, annotation.separator());
                    }
                }

            }

            return t;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 为字段设置值，支持基本类型 + List<String> + Set<String> + String[]
     * @param field 字段对象
     * @param obj 具体字段对应的对象
     * @param value 需要设置的值
     * @param separator 如果是集合或数组的话对 value 进行分割的分割符
     */
    static void setFieldValue(Field field, Object obj, Object value, String separator) {
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
                field.set(obj, Arrays.asList(value.toString().split(separator)));
            } else if (Set.class.isAssignableFrom(field.getType())) {
                field.set(obj, Arrays.stream(value.toString().split(separator)).collect(Collectors.toSet()));
            } else {
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
