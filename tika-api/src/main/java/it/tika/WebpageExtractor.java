package it.tika;

import flipdroid.grepper.Extractor;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.pipe.ImageFilter;
import flipdroid.grepper.pipe.PipeContentExtractor;
import flipdroid.grepper.pipe.TitleExtractorImpl;
import it.tika.exception.ExtractorException;
import it.tika.image.TikaImageService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 11:45 AM
 * <p/>
 * To change this template use File | Settings | File Templates.
 */
public class WebpageExtractor {
    private static WebpageExtractor instance;

    private List<Extractor> extractors;

    private WebpageExtractor() {
        extractors = new ArrayList<Extractor>();
        //todo extract the list initialization to other classes
        TitleExtractor extractor = new TitleExtractor();
        extractor.setTitleExtractor(new TitleExtractorImpl());

        ContentExtractor contentExtractor = new ContentExtractor();
        contentExtractor.setExtractor(new PipeContentExtractor());

        extractors.add(extractor);
        extractors.add(contentExtractor);
        extractors.add(new ImageFilter(new TikaImageService()));

    }

    public static WebpageExtractor getInstance() {
        if (instance == null) {
            instance = new WebpageExtractor();
        }
        return instance;
    }

    URLAbstract extract(URLAbstract urlAbstract) {
        if (urlAbstract == null || urlAbstract.getRawContent() == null || urlAbstract.getRawContent().length == 0) {
            return urlAbstract;
        }
        if (extractors == null || extractors.isEmpty()) {
            //TODO add log
            return urlAbstract;
        }
        try {
            for (Extractor extractor : extractors) {
                extractor.extract(urlAbstract);
            }
        } catch (ExtractorException e) {
            e.printStackTrace();
            return urlAbstract;//returns the urlAbstract so far
        }
        String title = urlAbstract.getTitle();
        String content = urlAbstract.getContent();

        if (title != null)
            if (content.length() > title.length() * 2 && content.indexOf(title) != -1) {
                urlAbstract.setContent(content.replaceFirst(title, ""));
            }


        return urlAbstract;
    }
}
