package it.tika;

import flipdroid.grepper.GrepperException;
import flipdroid.grepper.pipe.PipeContentExtractor;
import it.tika.exception.ExtractorException;

public class ContentExtractor implements Extractor {
    PipeContentExtractor extractor;

    public void setExtractor(PipeContentExtractor extractor) {
        this.extractor = extractor;
    }

    public void extract(URLAbstract urlAbstract) {
        try {
            String content = extractor.fireAbstract(urlAbstract.getRawContent(), urlAbstract.getCharset());
            urlAbstract.setContent(content);
            urlAbstract.setImages(extractor.getImages());
        } catch (GrepperException e) {
            throw new ExtractorException(e);
        }
    }
}
