package flipdroid.grepper.extractor.pipe;

import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.extractor.Extractor;
import flipdroid.grepper.extractor.ExtractorException;

public class GrepperContentExtractor implements Extractor {
    PipeGrepperContentExtractor extractor;

    public void setExtractor(PipeGrepperContentExtractor extractor) {
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
