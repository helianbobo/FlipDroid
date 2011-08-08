package flipdroid.grepper;

import flipdroid.grepper.pipe.*;
import flipdroid.grepper.pipe.GrepperContentExtractor;

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
    private List<Extractor> extractors;

    public WebpageExtractor(ImageService tikaImageService) {
        extractors = new ArrayList<Extractor>();
        GrepperTitleExtractor titleExtractor = new GrepperTitleExtractor();
        titleExtractor.setTitleExtractor(new TitleExtractorImpl());

        GrepperContentExtractor grepperContentExtractor = new GrepperContentExtractor();
        grepperContentExtractor.setExtractor(new PipeGrepperContentExtractor());

        extractors.add(titleExtractor);
        extractors.add(grepperContentExtractor);
        extractors.add(new ImageFilter(tikaImageService));

    }

    public URLAbstract extract(URLAbstract urlAbstract) {
        if (urlAbstract == null || urlAbstract.getRawContent() == null || urlAbstract.getRawContent().length == 0) {
            return urlAbstract;
        }
        if (extractors == null || extractors.isEmpty()) {
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
