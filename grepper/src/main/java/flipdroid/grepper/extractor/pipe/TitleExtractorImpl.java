package flipdroid.grepper.extractor.pipe;

import flipdroid.grepper.DetectionResult;
import flipdroid.grepper.FruitFetcher;
import flipdroid.grepper.GpathFromURIFetcher;
import flipdroid.grepper.extractor.TitleExtractor;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TitleExtractorImpl implements TitleExtractor {
    public String fireAbstract(byte[] data, Charset charset) {
        GpathFromURIFetcher gpathFetcher = new GpathFromURIFetcher();
        DetectionResult result = new FruitFetcher().fetch(gpathFetcher.fetch(data, charset));
        return result.getTitleText();
    }
}
