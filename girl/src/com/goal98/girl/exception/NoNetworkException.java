package com.goal98.girl.exception;

public class NoNetworkException extends RuntimeException{

    public NoNetworkException(Throwable cause) {
        super(cause);
    }

    public NoNetworkException() {
        super();
    }
}
