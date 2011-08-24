package it.tika.grepper;

import com.goal98.tika.common.Paragraphs;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/19/11
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class TikaOutputRegMatcherTest extends TestCase {
    public void testMatcher() throws IOException, BoilerpipeProcessingException, SAXException {
//
        System.out.println("<br/><br/>".replaceAll("(<br/>)+", "<br/>"));
    }
}
