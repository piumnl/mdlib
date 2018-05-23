package cn.piumnl.mdlib.entity;

import java.util.List;
import java.util.UUID;

import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-22.
 */
public class CodeTree {

    private String id;

    private String name;

    private String url;

    private String parent;

    private List<CodeTree> children;

    public CodeTree(String name) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = name;
    }

    public CodeTree(String name, CodeTree parent) {
        this(name);
        this.url = StringUtil.get(parent.getUrl()) + "/" + name;
        this.parent = parent.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<CodeTree> getChildren() {
        return children;
    }

    public void setChildren(List<CodeTree> children) {
        this.children = children;
    }

    public String getParent() {
        return parent;
    }
}
