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
        final BoilerpipeExtractor extractorChinese;
        final BoilerpipeExtractor extractor;

        final HTMLHighlighter hhChinese;
        final HTMLHighlighter hh;

        extractorChinese = CommonExtractors.CHINESE_ARTICLE_EXTRACTOR;
        hhChinese = HTMLHighlighter.newExtractingInstanceForChinese();

        extractor = CommonExtractors.ARTICLE_EXTRACTOR;
        hh = HTMLHighlighter.newExtractingInstance();
        try{
           return hhChinese.process(new HTMLDocument(data, charset), extractorChinese);
        }catch(Exception e){
            throw new GrepperException(e);
        }
    }
}
