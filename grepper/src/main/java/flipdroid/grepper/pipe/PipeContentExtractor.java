package flipdroid.grepper.pipe;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import flipdroid.grepper.ContentExtractor;
import flipdroid.grepper.GrepperException;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class PipeContentExtractor implements ContentExtractor {
    public String fireAbstract(byte[] data, Charset charset) throws GrepperException {
        final BoilerpipeExtractor extractor;
        final HTMLHighlighter hh;


        String string = new String(data, charset);
        char[] text = string.toCharArray();
        boolean containsChinese = false;
        for (int i = 0; i < text.length; i++) {
            if ((text[i] >= 0x4e00) && (text[i] <= 0x9fbb)) {
                containsChinese = true;
                break;
            }
        }
        try {
            if (containsChinese) {
                extractor = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
                hh = HTMLHighlighter.newExtractingInstanceForChinese();
            } else {
                extractor = CommonExtractors.ARTICLE_EXTRACTOR;
                hh = HTMLHighlighter.newExtractingInstance();
            }
            return hh.process(new HTMLDocument(data, charset), extractor).trim();
        } catch (Exception e) {
            throw new GrepperException(e);
        }
    }
}
