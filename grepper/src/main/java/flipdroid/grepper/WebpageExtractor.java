package flipdroid.grepper;

import flipdroid.grepper.extractor.Extractor;
import flipdroid.grepper.extractor.ExtractorException;
import flipdroid.grepper.extractor.image.ImageService;
import flipdroid.grepper.extractor.image.TikaImageService;
import flipdroid.grepper.extractor.pipe.*;
import flipdroid.grepper.extractor.raw.URLRawRepo;
import it.tika.TikaException;
import it.tika.TikaResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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

    public static void main(String[] args) throws IOException {
        WebpageExtractor extractor = new WebpageExtractor(null);
        TikaResponse response = new TikaResponse();


        String urlDecoded = java.net.URLDecoder.decode("http://www.ifanr.com/49120", "UTF-8");
        byte[] rawBytes = URLRawRepo.getInstance().fetch(urlDecoded);
        String charset = EncodingDetector.detect(new BufferedInputStream(new ByteArrayInputStream(rawBytes)));
        Charset cs = null;
        if (charset != null)
            cs = Charset.forName(charset);
        else {
            try {
                cs = Charset.forName("utf-8");
            } catch (UnsupportedCharsetException e) {
            }
        }

        if (rawBytes == null) {
            response.setSuccess(false);
        } else {
            URLAbstract result = new URLAbstract(rawBytes, cs);
            result.setUrl(urlDecoded);
            result = extractor.extract(result);
            response.setContent(result.getContent());
            response.setImages(result.getImages());
            response.setTitle(result.getTitle());
            response.setSuccess(true);
        }
        System.out.println(response.getContent());

    }
}
