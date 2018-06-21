package cn.piumnl.mdlib.handler;

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

    protected static List<Article> getArticles(Site render, Library lib, List<String> allFile) {
        // 指定要生成的目录
        List<String> filterList =
                lib.getDir()
                   .stream()
                   .map(s -> render.getOut().toAbsolutePath().resolve(Paths.get(s).toFile().getName()).toString())
                   .collect(Collectors.toList());
        // 对所有已渲染的文件进行过滤
        List<Article> collect = new ArrayList<>();
        for (String file : allFile) {
            for (String filter : filterList) {
                if (file.startsWith(filter)) {
                    collect.add(new Article(getRelativizePath(render, file)));
                    break;
                }
            }
        }
        return collect;
    }

    protected static Path getRelativizePath(Site render, String renderFilePath) {
        return render.getOut().toAbsolutePath().relativize(Paths.get(renderFilePath));
    }

    protected static Path resolvePath(Site render, String resolvePath) {
        return render.getOut().resolve(resolvePath);
    }
}
