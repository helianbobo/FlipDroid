package it.tika;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.extractors.ExtractorBase;
import it.tika.exception.ExtractorException;

public class ContentExtractor {

    private static ContentExtractor instance;

    private ContentExtractor(){

    }

    public static ContentExtractor getInstance(){
        if(instance == null){
            instance = new ContentExtractor();
        }
        return instance;
    }

    public String extract(TextDocument raw) throws ExtractorException{

        ExtractorBase extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        //TODO: shaoli
        String result = null;

        try {

            result = extractor.getText(raw);


        } catch (BoilerpipeProcessingException e) {
            throw new ExtractorException(e);
        }

        return result;
    }

}
