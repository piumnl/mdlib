package cn.piumnl.mdlib.util;

import java.util.Properties;

import org.junit.Test;

import cn.piumnl.mdlib.entity.MdlibProperties;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-20.
 */
public class RefelectUtilTest {

    @Test
    public void inject() {
        Properties properties = new Properties();
        properties.setProperty("lib.name", "piumnl");
        properties.setProperty("lib.icon", "/icon");

        MdlibProperties inject = RefelectUtil.inject(properties, MdlibProperties.class);
        System.out.println(inject);
    }
}