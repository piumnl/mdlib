package cn.piumnl.mdlib.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-03-25.
 */
public class ArchiveIndex implements Serializable, Comparable<ArchiveIndex> {

    private String name;

    private List<Article> articleList;

    public ArchiveIndex(String name, List<Article> articleList) {
        this.name = name;
        this.articleList = articleList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArchiveIndex that = (ArchiveIndex) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return articleList != null ? articleList.equals(that.articleList) : that.articleList == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (articleList != null ? articleList.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ArchiveIndex index) {
        return this.getName().compareTo(index.getName());
    }
}
