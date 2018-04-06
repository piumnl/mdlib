package cn.piumnl.mdlib.entity;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class Article implements Serializable, Comparable<Article> {

    private String name;

    private String url;

    public Article(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Article(Path path) {
        this.name = path.toFile().getName();
        this.name = this.name.substring(0, this.name.lastIndexOf("."));
        this.url = path.toString();
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

    @Override
    public String toString() {
        return "Article{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int compareTo(Article article) {
        return this.getName().compareTo(article.getName());
    }
}
