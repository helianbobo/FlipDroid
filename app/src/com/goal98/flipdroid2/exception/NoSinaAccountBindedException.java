package com.goal98.flipdroid2.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/6/11
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoSinaAccountBindedException extends Exception {
    public NoSinaAccountBindedException() {
    }

    public NoSinaAccountBindedException(Throwable cause) {
        super(cause);
    }

    public NoSinaAccountBindedException(String message) {
        super(message);
    }

    public NoSinaAccountBindedException(String message, Throwable cause) {
        super(message, cause);
    }
}
