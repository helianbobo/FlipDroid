package it.tika;

import flipdroid.grepper.Extractor;
import flipdroid.grepper.GrepperException;
import flipdroid.grepper.URLAbstract;
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

            if (urlAbstract.getTitle() == null || urlAbstract.getTitle().length() == 0)//in case title fetcher failed
                urlAbstract.setTitle(extractor.getTitle());
        } catch (GrepperException e) {
            throw new ExtractorException(e);
        } catch (Exception e) {
            throw new ExtractorException(e);
        }
    }
}
