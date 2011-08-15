package flipdroid.grepper;

import flipdroid.grepper.extractor.Extractor;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.pipe.*;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import it.tika.mongodb.image.ImageService;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*></[^>]*>");
   public static final Pattern PAT_SUPER_TAG = Pattern.compile("^<[^>]*>(<.*?>)</[^>]*>$");
    public static void main(String[] args) {
        String html = "<p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><H1></H1><p></p><p><p>";
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            Matcher m = PAT_TAG_NO_TEXT.matcher(html);
            if (m.find()) {
                repeat = true;
                html = m.replaceAll("");
            }
             m = PAT_SUPER_TAG.matcher(html);
                if (m.find()) {
                    repeat = true;
                    html = m.replaceAll(m.group(1));
                }

        }
        System.out.println(html);
    }
}
