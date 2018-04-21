package cn.piumnl.mdlib.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class ListTemplate extends AbstractLibraryTemplate {

    private List<Article> articles;

    public ListTemplate(Site site, String name, List<Article> articles) {
        super(site, name, "list.ftl");
        this.articles = articles;
    }

    @Override
    public Map<String, Object> dataModel() {
        Map<String, Object> listLibMap = new HashMap<>(6);

        initSiteModal(listLibMap);
        listLibMap.put("siteName", getSite().getName());
        listLibMap.put("libraries", getSite().getLibraries());
        listLibMap.put("articles", articles);

        return listLibMap;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
