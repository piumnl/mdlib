package cn.piumnl.mdlib.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-24.
 */
public class IndexHandler implements Handler {

    private static final IndexHandler HANDLER = new IndexHandler();

    private IndexHandler() {
    }

    public static IndexHandler getInstance() {
        return HANDLER;
    }

    @Override
    public void process(Site site) throws Exception {
        genIndexPage(site);
    }

    @Override
    public void refresh(Site site) throws Exception {
        genIndexPage(site);
    }

    private void genIndexPage(Site site) throws IOException {
        Library library = site.getLibraries().get(0);
        Path sourceFile = site.getOut().resolve(library.getName() + ".html");
        Path targetFile = site.getOut().resolve("index.html");
        Files.deleteIfExists(targetFile);
        Files.copy(sourceFile, targetFile);
    }
}
