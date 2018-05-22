package cn.piumnl.mdlib.entity;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static final String STATIC_ML_ICO = "static/ml.ico";

    private MdlibProperties properties;

    private List<Library> list;

    private List<Library> collapsible;

    private List<Library> single;

    private transient Path out;

    private List<File> staticPath;

    private String codePath;

    public Site(MdlibProperties properties) {
        this.properties = properties;

        this.list = convertLibrary(properties.getList().stream(), "list");
        this.collapsible = convertLibrary(properties.getCollapsible().stream(), "collapsible");
        this.single = convertLibrary(properties.getSingle().stream(), "single");
        this.codePath = properties.getCode();

        this.out = Paths.get(properties.getOutPath());

        this.staticPath = getMdStaticPath(properties);
    }

    private List<Library> convertLibrary(Stream<String> stream, String type) {
        return stream.filter(StringUtil::isNotEmpty)
                     .collect(ArrayList::new, (list, lib) -> list.add(new Library(lib, type)), List::addAll);
    }

    private List<File> getMdStaticPath(MdlibProperties properties) {
        return Arrays.stream(properties.getResourcePaths())
                     .map(File::new)
                     .collect(Collectors.toList());
    }

    public List<Library> getLibraries() {
        List<Library> listLib = getList();
        List<Library> collapsibleLib = getCollapsible();
        List<Library> singleLib = getSingle();

        int initialCapacity = listLib.size() + collapsibleLib.size() + singleLib.size();
        List<Library> libraries = new ArrayList<>(initialCapacity);

        libraries.addAll(listLib);
        libraries.addAll(collapsibleLib);
        libraries.addAll(singleLib);
        return libraries;
    }

    public String getIcon() {
        return properties.getIcon();
    }

    public List<Library> getList() {
        return list;
    }

    public void setList(List<Library> list) {
        this.list = list;
    }

    public String getName() {
        return properties.getName();
    }

    public List<Library> getCollapsible() {
        return collapsible;
    }

    public void setCollapsible(List<Library> collapsible) {
        this.collapsible = collapsible;
    }

    public String getUri() {
        return properties.getIndexURI();
    }

    public List<File> getStaticPath() {
        return staticPath;
    }

    public Path getOut() {
        return out;
    }

    public boolean isDefaultStaticPath() {
        return properties.isNeedDefaultResourcePath();
    }

    public List<Library> getSingle() {
        return single;
    }

    public String getCodePath() {
        return codePath;
    }
}
