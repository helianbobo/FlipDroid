package de.l3s.boilerpipe.demo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

/**
 * Demonstrates how to use Boilerpipe to get the main content, highlighted as
 * HTML.
 *
 * @author Christian Kohlschütter
 * @see Oneliner if you only need the plain text.
 */
public class HTMLHighlightDemo {
    public static void main(String[] args) throws Exception {
//        URL url = new URL("http://www.ifanr.com/48042");
//
//        final BoilerpipeExtractor extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
//        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstanceForChinese();
//
//        String process = hh.process(url, extractor);
//
//        System.out.println(process);

        String a = "<p></p><img><p></p>";
        List<String> result = toParagraph(a);
        System.out.println(result.size());
    }

    public static List toParagraph(String articleContent) {
        List<String> paragraphs = new ArrayList<String>();
        if (articleContent == null)
            return paragraphs;

        int cutAt = 0;
        while ((cutAt = articleContent.indexOf("</p>")) != -1) {
            String paragraph = articleContent.substring(0, cutAt + 4);
            while (paragraph.startsWith("<img>")) {
                paragraphs.add("<img>");
                paragraph = paragraph.substring("<img>".length());
            }
            paragraphs.add(paragraph);
            articleContent = articleContent.substring(cutAt + 4);
        }
        while (articleContent.startsWith("<img>")) {
            paragraphs.add("<img>");
            articleContent = articleContent.substring("<img>".length());
        }
        return paragraphs;
    }


}
