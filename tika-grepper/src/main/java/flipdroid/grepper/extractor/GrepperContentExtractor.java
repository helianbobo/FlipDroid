package flipdroid.grepper.extractor;

import flipdroid.grepper.extractor.pipe.GrepperException;
import flipdroid.grepper.extractor.pipe.PipeGrepperContentExtractor;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GrepperContentExtractor {
    ExtractResult fireAbstract(byte[] data, Charset charset) throws GrepperException;

    class ExtractResult {
        public List<String> images;
        public String title;
        public String abstractText;
    }
}
