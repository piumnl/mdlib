package cn.piumnl.mdlib.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

/*************************************************************
 * function - 
 * Copyright(c)
 * @author ex-zhongziqi001 2018/09/28
 * @date 2018/09/28
 * @version 1.0
 *************************************************************/
public class ParseMd {

    /**
     * 测试parse()方法
     * @since 2018/9/28
     */
    @Test
    public void test_parse() throws IOException {
        Path path = Paths.get(System.getProperty("user.home"), "Downloads", "README.md");

        List<Extension> extensions = new ArrayList<>();
        extensions.add(TablesExtension.create());
        extensions.add(SimTocExtension.create());
        extensions.add(AnchorLinkExtension.create());
        extensions.add(FootnoteExtension.create());

        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, extensions);
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        Node document = parser.parse(lines.stream().reduce((s, s2) -> s + "\n" + s2).orElseThrow(() -> new RuntimeException("读取失败")));
        String render = renderer.render(document);
        String html = "<html><head><link rel=\"stylesheet\" href=\"github.css\" type=\"text/css\"/></head><body>" + render + "</body></html>";
        Files.write(path.resolveSibling("test.html"), html.getBytes(StandardCharsets.UTF_8));
    }
}
