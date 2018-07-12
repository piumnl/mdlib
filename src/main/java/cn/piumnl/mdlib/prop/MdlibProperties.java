package cn.piumnl.mdlib.prop;

import java.util.Arrays;
import java.util.List;

import cn.piumnl.mdlib.annotation.Property;
import cn.piumnl.mdlib.entity.Library;

/**
 * 注入 application.properties 文件的值到本类中
 *
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-19.
 */
public class MdlibProperties {

    /**
     * 站点名称
     */
    @Property(value = "lib.name", defaultValue = "mdlib")
    private String name;

    /**
     * 站点图标
     */
    @Property(value = "lib.icon", defaultValue = "static/ml.ico")
    private String icon;

    /**
     * 站点地址前缀
     */
    @Property(value = "lib.uri")
    private String indexURI;

    /**
     * 资源目录
     */
    @Property(value = "lib.resource-path")
    private String[] resourcePaths;

    /**
     * 是否加载默认提供的资源
     */
    @Property(value = "lib.resource-default", defaultValue = "true")
    private boolean needDefaultResourcePath;

    /**
     * 列表类型的文章排列
     */
    @Property(value = "lib.mixed", isPrefix = true)
    private List<Library> list;

    /**
     * 可折叠类型的文章排列
     */
    @Property(value = "lib.module", isPrefix = true)
    private List<Library> collapsible;

    /**
     * 单文章页
     */
    @Property(value = "lib.single", isPrefix = true)
    private List<Library> single;

    /**
     * 输出目录
     */
    @Property(value = "lib.out", defaultValue = "mdlib")
    private String outPath;

    /**
     * 代码所在目录
     */
    @Property(value = "lib.code", defaultValue = "code")
    private String code;

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

    public List<Library> getList() {
        return list;
    }

    public List<Library> getCollapsible() {
        return collapsible;
    }

    public List<Library> getSingle() {
        return single;
    }

    public String getCode() {
        return code;
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
                ", code='" + code + '\'' +
                '}';
    }
}
