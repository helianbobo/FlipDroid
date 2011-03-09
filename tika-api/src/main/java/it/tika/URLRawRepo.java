package it.tika;

public class URLRawRepo {

    private static URLRawRepo instance;

    private URLRawRepo(){

    }

    public static URLRawRepo getInstance(){
        if(instance == null){
            instance = new URLRawRepo();
        }
        return instance;
    }

    public String fetch(String url){
        return null;
    }

}
