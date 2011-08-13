package flipdroid.grepper.extractor.pipe;

import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.extractor.Extractor;
import flipdroid.grepper.extractor.TitleExtractor;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrepperTitleExtractor implements Extractor {
    public void setTitleExtractor(TitleExtractor titleExtractor) {
        this.titleExtractor = titleExtractor;
    }

    private TitleExtractor titleExtractor;

    public void extract(URLAbstract urlAbstract) {
        String title = titleExtractor.fireAbstract(urlAbstract.getRawContent(), urlAbstract.getCharset());
        urlAbstract.setTitle(title);
    }
}
