package cn.piumnl.mdlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入 Property 的属性
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-19.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    /**
     * Property 的属性名
     * @return Property 的属性名
     */
    String value();

    /**
     * 提供默认值
     * @return 提供默认值
     */
    String defaultValue() default "";

    /**
     * 针对数组字段的分隔符
     * @return 针对数组字段的分隔符
     */
    String separator() default ",";
}
