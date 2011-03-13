package it.tika;

import flipdroid.grepper.pipe.TitleExtractorImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/13/11
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TitleExtractor implements Extractor{
    public void setTitleExtractor(flipdroid.grepper.TitleExtractor titleExtractor) {
        this.titleExtractor = titleExtractor;
    }

    private flipdroid.grepper.TitleExtractor titleExtractor;

    public void extract(URLAbstract urlAbstract) {
        String title = titleExtractor.fireAbstract(urlAbstract.getRawContent(), urlAbstract.getCharset());
        urlAbstract.setTitle(title);
    }
}
