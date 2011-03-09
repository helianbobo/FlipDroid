package it.tika;

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

    public URLAbstract extract(String raw){
        //TODO: shaoli
        return null;
    }

}
