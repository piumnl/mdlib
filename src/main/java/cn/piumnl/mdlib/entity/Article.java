package cn.piumnl.mdlib.entity;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class Article implements Serializable, Comparable<Article> {

    private static final long serialVersionUID = 2914595993457310665L;

    private String name;

    private String url;

    private Long updateTime;

    public Article(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Article(Path path, String name, long lastModified) {
        this.name = name;
        int endIndex = this.name.lastIndexOf(".");
        if (endIndex != -1) {
            this.name = this.name.substring(0, endIndex);
        }
        this.url = path.toString();
        this.updateTime = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public int compareTo(Article article) {
        return this.getName().compareTo(article.getName());
    }

    @Override
    public String toString() {
        return "Article{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
