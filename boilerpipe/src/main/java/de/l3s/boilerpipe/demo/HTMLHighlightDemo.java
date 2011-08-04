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
//        URL url = new URL("http://www.36kr.com/google-apple-attempting-to-%E2%80%9Cstrangle%E2%80%9D-android/");
//
//        final BoilerpipeExtractor extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
//        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstanceForChinese();
//
//        String process = hh.process(url, extractor);
//
//        System.out.println(process);

        String a = "<img src=http://www.36kr.com/wp-content/themes/36kr/images/share/sina.gif?20X20/>ssss";
//        List<String> result = toParagraph(a);
//        System.out.println(result.size());

        System.out.println(a.replaceAll("<img src=[.]+]",""));
    }

    public static List toParagraph(String articleContent) {
        List<String> paragraphs = new ArrayList<String>();
        if (articleContent == null)
            return paragraphs;

        int cutAt = 0;
        articleContent = parseImg(paragraphs, articleContent);
        while ((cutAt = articleContent.indexOf("</p>")) != -1) {
            String paragraph = articleContent.substring(0, cutAt + 4);
            paragraph = parseImg(paragraphs, paragraph);
            paragraphs.add(paragraph);
            articleContent = articleContent.substring(cutAt + 4);
        }
        articleContent = parseImg(paragraphs, articleContent);
        return paragraphs;
    }

    private static String parseImg(List<String> paragraphs, String paragraph) {
        while (paragraph.startsWith("<img")) {
            int endOfImg = paragraph.indexOf("/>");
            paragraphs.add(paragraph.substring(0,endOfImg+2));
            paragraph = paragraph.substring(endOfImg+2);
        }
        return paragraph;
    }


}
