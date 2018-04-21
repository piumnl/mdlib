package cn.piumnl.mdlib.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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

    static Logger LOGGER = Logger.getLogger("cn.piumnl.mdlib");

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
                    value = properties.getProperty(annotation.value(), annotation.defaultValue());
                    setFieldValue(field, t, value, annotation.separator());
                }
            }

            return t;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    static void setFieldValue(Field field, Object obj, String value, String separator) {
        try {
            field.setAccessible(true);

            if (field.getType() == int.class || field.getType() == Integer.class) {
                field.setInt(obj, Integer.parseInt(value));
            } else if (field.getType() == short.class || field.getType() == Short.class) {
                field.setShort(obj, Short.parseShort(value));
            } else if (field.getType() == byte.class || field.getType() == Byte.class) {
                field.setByte(obj, Byte.parseByte(value));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.setBoolean(obj, Boolean.parseBoolean(value));
            } else if (field.getType() == long.class || field.getType() == Long.class) {
                field.setLong(obj, Long.parseLong(value));
            } else if (field.getType() == float.class || field.getType() == Float.class) {
                field.setFloat(obj, Float.parseFloat(value));
            } else if (field.getType() == double.class || field.getType() == Double.class) {
                field.setDouble(obj, Double.parseDouble(value));
            } else if (field.getType() == char.class || field.getType() == Character.class) {
                field.setChar(obj, value.charAt(0));
            } else if (field.getType().isArray()) {
                field.set(obj, value.split(separator));
            } else if (List.class.isAssignableFrom(field.getType())) {
                field.set(obj, Arrays.asList(value.split(separator)));
            } else if (Set.class.isAssignableFrom(field.getType())) {
                field.set(obj, Arrays.stream(value.split(separator)).collect(Collectors.toSet()));
            } else {
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
