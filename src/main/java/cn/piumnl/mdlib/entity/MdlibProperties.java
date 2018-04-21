package cn.piumnl.mdlib.entity;

import java.util.Arrays;
import java.util.List;

import cn.piumnl.mdlib.annotation.Property;

/**
 * 注入 application.properties 文件的值到本类中
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-19.
 */
public class MdlibProperties {

    @Property(value = "lib.name", defaultValue = "mdlib")
    private String name;

    @Property(value = "lib.icon", defaultValue = "static/ml.ico")
    private String icon;

    @Property(value = "lib.uri")
    private String indexURI;

    @Property(value = "lib.resource-path")
    private String[] resourcePaths;

    @Property(value = "lib.resource-default", defaultValue = "true")
    private boolean needDefaultResourcePath;

    @Property(value = "lib.mixed")
    private List<String> list;

    @Property(value = "lib.module")
    private List<String> collapsible;

    @Property(value = "lib.single")
    private List<String> single;

    @Property(value = "lib.out",defaultValue = "mdlib")
    private String outPath;

    public MdlibProperties() {
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getIndexURI() {
        return indexURI;
    }

    public String[] getResourcePaths() {
        return resourcePaths;
    }

    public boolean isNeedDefaultResourcePath() {
        return needDefaultResourcePath;
    }

    public String getOutPath() {
        return outPath;
    }

    public List<String> getList() {
        return list;
    }

    public List<String> getCollapsible() {
        return collapsible;
    }

    public List<String> getSingle() {
        return single;
    }

    @Override
    public String toString() {
        return "MdlibProperties{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", indexURI='" + indexURI + '\'' +
                ", resourcePaths=" + Arrays.toString(resourcePaths) +
                ", needDefaultResourcePath=" + needDefaultResourcePath +
                ", list=" + list +
                ", collapsible=" + collapsible +
                ", single=" + single +
                ", outPath='" + outPath + '\'' +
                '}';
    }
}
