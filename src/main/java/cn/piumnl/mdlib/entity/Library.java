package cn.piumnl.mdlib.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-05.
 */
public class Library implements Serializable {

    private String name;

    private List<String> dir;

    private String url;

    public Library(String lib, String urlPrefix) {
        if (StringUtil.isEmpty(lib)) {
            throw new RuntimeException("lib is empty!");
        }

        initNameAndDir(lib);
        this.url = urlPrefix + "-" + this.name +".html";
    }

    public Library(String name, List<String> dir, String type) {
        if (StringUtil.isEmpty(name)) {
            throw new RuntimeException("lib is empty!");
        }
        Objects.requireNonNull(dir);

        this.name = name;
        this.dir = dir;
        this.url = name + ".html";
    }

    private void initNameAndDir(String lib) {
        String[] split = lib.split(":");
        if (split.length != 2) {
            throw new RuntimeException(StringUtil.format("lib format invalid: '{}'", lib));
        }

        String path = split[1];
        this.dir = Collections.singletonList(path.startsWith("/") ? path.substring(1) : path);
        this.name = split[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDir() {
        return dir;
    }

    public void setDir(List<String> dir) {
        this.dir = dir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
