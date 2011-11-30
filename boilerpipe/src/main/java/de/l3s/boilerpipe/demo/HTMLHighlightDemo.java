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

        URL url = new URL("http://sports.sina.com.cn/j/2011-11-30/02115849354.shtml");

        final BoilerpipeExtractor extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstanceForChinese();

        String process = hh.process(url, extractor);
        System.out.println(process);

    }






}
