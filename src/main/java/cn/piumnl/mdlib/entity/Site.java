package cn.piumnl.mdlib.entity;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        this.list = convertLibrary(properties.getList());
        this.collapsible = convertLibrary(properties.getCollapsible());
        this.single = convertLibrary(properties.getSingle());
        this.codePath = properties.getCode();

        this.out = Paths.get(properties.getOutPath());

        this.staticPath = getMdStaticPath(properties);
    }

    private List<Library> convertLibrary(Map<String, List<String>> stream) {
        List<Library> result = new ArrayList<>(stream.size());
        for (Map.Entry<String, List<String>> entry : stream.entrySet()) {
            if (entry.getValue() != null && entry.getValue().size() > 0) {
                result.add(new Library(entry.getKey(), entry.getValue()));
            }
        }

        return result;
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

        Path codePath = Paths.get(getCodePath());
        if (Files.exists(codePath)) {
            libraries.add(new Library("代码库", Collections.singletonList("code")));
        }
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
