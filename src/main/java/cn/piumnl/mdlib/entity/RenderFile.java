package cn.piumnl.mdlib.entity;

import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0
 * @date 2018/07/10
 */
public class RenderFile {

    private String title;

    private String content;

    public RenderFile(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasTitle() {
        return StringUtil.isNotEmpty(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RenderFile{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
