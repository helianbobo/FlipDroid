package flipdroid.grepper.exception;

public class DBNotAvailableException extends RuntimeException{

    public DBNotAvailableException(Exception e){
        super(e);
    }
}
