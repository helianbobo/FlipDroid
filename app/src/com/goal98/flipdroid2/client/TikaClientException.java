package com.goal98.flipdroid2.client;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/20/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaClientException extends Exception {
    public TikaClientException(Exception e) {
        super(e);
    }

    public TikaClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TikaClientException() {
        super();
    }
}
