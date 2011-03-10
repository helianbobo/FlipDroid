package it.tika.exception;


public class ExtractorException extends RuntimeException{
    public ExtractorException(Exception e){
        super(e);
    }
}
