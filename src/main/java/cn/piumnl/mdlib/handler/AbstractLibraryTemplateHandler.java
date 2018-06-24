package cn.piumnl.mdlib.handler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.piumnl.mdlib.entity.Article;
import cn.piumnl.mdlib.entity.Library;
import cn.piumnl.mdlib.entity.Site;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-06-21.
 */
public abstract class AbstractLibraryTemplateHandler implements Handler {

    protected List<Article> getArticles(Site render, Library lib) {
        // 指定要生成的目录
        List<String> filterList =
                lib.getDir()
                   .stream()
                   .map(s -> render.getOut().toAbsolutePath().resolve(Paths.get(s).toFile().getName()).normalize().toString())
                   .collect(Collectors.toList());
        // 对所有已渲染的文件进行过滤
        List<Article> collect = new ArrayList<>();
        for (String article : filterList) {
            int libDir = article.length();
            File file = new File(article);
            if (file.isDirectory()) {
                findDirectoryFile(file, collect, libDir, lib.getName());
            } else {
                collect.add(new Article(Paths.get(lib.getName(), file.getAbsolutePath().substring(libDir))));
            }
        }
        return collect;
    }

    private void findDirectoryFile(File dir, List<Article> collect, int site, String libName) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findDirectoryFile(file, collect, site, libName);
            } else if (file.isFile()) {
                collect.add(new Article(Paths.get(libName,file.getAbsolutePath().substring(site))));
            }
        }
    }

    protected static Path resolvePath(Site render, String resolvePath) {
        return render.getOut().resolve(resolvePath);
    }
}
