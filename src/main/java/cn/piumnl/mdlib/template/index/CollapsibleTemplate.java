package cn.piumnl.mdlib.template.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.piumnl.mdlib.entity.ArchiveIndex;
import cn.piumnl.mdlib.entity.Site;
import cn.piumnl.mdlib.template.AbstractLibraryTemplate;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-04-06.
 */
public class CollapsibleTemplate extends AbstractLibraryTemplate {

    private List<ArchiveIndex> archiveIndices;

    public CollapsibleTemplate(Site site, String name, List<ArchiveIndex> archiveIndices) {
        super(site, name, "collapsible.ftl");
        this.archiveIndices = archiveIndices;
    }

    @Override
    public Map<String, Object> dataModel() {
        HashMap<String, Object> listLibMap = new HashMap<>(6);

        initSiteModal(listLibMap);
        listLibMap.put("siteName", getSite().getName());
        listLibMap.put("libraries", getSite().getLibraries());
        listLibMap.put("bookshelf", archiveIndices);

        return listLibMap;
    }

    public List<ArchiveIndex> getArchiveIndices() {
        return archiveIndices;
    }
}
