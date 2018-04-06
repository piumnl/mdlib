package cn.piumnl.mdlib.entity;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-05.
 */
public class Site implements Serializable {

    private static final long serialVersionUID = -1764513206170007080L;

    private static final String TRUE = "true";

    private List<File> staticPath;

    private boolean defaultStaticPath = false;

    private String name;

    private String icon;

    private String uri;

    private List<Library> list;

    private List<Library> collapsible;

    private List<Library> single;

    private Path out;

    public Site(Properties properties) {
        this.name = properties.getProperty("name", "mdlib");
        this.uri = properties.getProperty("uri", "");
        this.out = Paths.get(properties.getProperty("out", "mdlib"));

        String property = properties.getProperty("static");
        if (StringUtil.isEmpty(property)) {
            this.staticPath = new ArrayList<>();
        } else {
            this.staticPath = Arrays.stream(property.split(","))
                                    .map(File::new)
                                    .collect(Collectors.toList());
        }

        // 是否提供默认的静态资源
        String defaultStatic = properties.getProperty("need-default-static", TRUE);
        if (TRUE.equalsIgnoreCase(defaultStatic)) {
            this.defaultStaticPath = true;
        }
        // 是否提供默认的静态资源
        this.icon = properties.getProperty("icon");
        if (StringUtil.isEmpty(this.icon)) {
            this.icon = "static/ml.ico";
        }

        String li = "list";
        this.list = Stream.of(properties.getProperty(li, ""))
                          .filter(StringUtil::isNotEmpty)
                          .flatMap(s -> Arrays.stream(s.split(",")))
                          .collect(ArrayList::new, (list, lib) -> list.add(new Library(lib, li)), List::addAll);

        String coll = "collapsible";
        this.collapsible = Stream.of(properties.getProperty(coll, ""))
                                 .filter(StringUtil::isNotEmpty)
                                 .flatMap(s -> Arrays.stream(s.split(",")))
                                 .collect(ArrayList::new, (list, lib) -> list.add(new Library(lib, coll)), List::addAll);

        String single = "single";
        this.single = Stream.of(properties.getProperty(single, ""))
                            .filter(StringUtil::isNotEmpty)
                            .flatMap(s -> Arrays.stream(s.split(",")))
                            .collect(ArrayList::new, (list, lib) -> list.add(new Library(lib, single)), List::addAll);
    }

    public List<Library> getLibraries() {
        List<Library> list = getList();
        List<Library> collapsible = getCollapsible();
        List<Library> single = getSingle();

        List<Library> libraries = new ArrayList<>(list.size() + collapsible.size());

        libraries.addAll(list);
        libraries.addAll(collapsible);
        libraries.addAll(single);
        return libraries;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Library> getList() {
        return list;
    }

    public void setList(List<Library> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public List<Library> getCollapsible() {
        return collapsible;
    }

    public void setCollapsible(List<Library> collapsible) {
        this.collapsible = collapsible;
    }

    public String getUri() {
        return uri;
    }

    public List<File> getStaticPath() {
        return staticPath;
    }

    public Path getOut() {
        return out;
    }

    public boolean isDefaultStaticPath() {
        return defaultStaticPath;
    }

    public List<Library> getSingle() {
        return single;
    }
}
