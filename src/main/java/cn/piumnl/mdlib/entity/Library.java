package cn.piumnl.mdlib.entity;

import java.io.Serializable;
import java.util.Objects;

import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-05.
 */
public class Library implements Serializable {

    /**
     * 库名，比如 Java、JavaScript 等，存在于页面的导航栏中。
     */
    private String name;

    /**
     * 该库所存放的文件所在目录，此处可以为多个目录
     */
    private String dir;

    /**
     * 页面地址
     */
    private String url;

    public Library(String name, String dir) {
        if (StringUtil.isEmpty(name)) {
            throw new RuntimeException("lib is empty!");
        }
        Objects.requireNonNull(dir);

        this.name = name;
        this.dir = dir;
        this.url = name + ".html";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Library library = (Library) o;

        if (name != null ? !name.equals(library.name) : library.name != null) return false;
        if (dir != null ? !dir.equals(library.dir) : library.dir != null) return false;
        return url != null ? url.equals(library.url) : library.url == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Library{" +
                "name='" + name + '\'' +
                ", dir='" + dir + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
