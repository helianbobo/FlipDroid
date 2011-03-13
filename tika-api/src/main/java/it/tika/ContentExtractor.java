package it.tika;

import flipdroid.grepper.GrepperException;
import it.tika.exception.ExtractorException;

public class ContentExtractor implements Extractor {
    flipdroid.grepper.ContentExtractor extractor;

    public void setExtractor(flipdroid.grepper.ContentExtractor extractor) {
        this.extractor = extractor;
    }

    public void extract(URLAbstract urlAbstract) {
        try {
            urlAbstract.setContent(extractor.fireAbstract(urlAbstract.getRawContent(),urlAbstract.getCharset()));
        } catch (GrepperException e) {
            throw new ExtractorException(e);
        }
    }
}
