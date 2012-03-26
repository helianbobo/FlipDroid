package de.l3s.boilerpipe.demo;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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

        URL url = new URL("http://www.36kr.com/p/92876.html");

        final BoilerpipeExtractor extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstanceForChinese();

        String process = hh.process(url, extractor);
        System.out.println(process);

//        URL url = new URL("http://img05.36krcnd.com/wp-content/uploads/2011/12/印象码logo.gif");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        int fileSize = connection.getContentLength();
//
//        byte[] imageData = new byte[fileSize];
//
//        BufferedInputStream istream = new BufferedInput/Users/jleo/IdeaProjects/FlipDroid/Stream(connection.getInputStream(), 32768);
//        try {
//            int bytesRead = 0;
//            int offset = 0;
//            while (bytesRead != -1 && offset < fileSize) {
//                bytesRead = istream.read(imageData, offset, fileSize - offset);
//                offset += bytesRead;
//            }
//        } finally {
//            istream.close();
//            connection.disconnect();
//        }

        // clean up
    }


}
