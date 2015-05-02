package com.umb.cs682.projectlupus.exceptions;

/**
 * Created by Nithya Kiran on 5/2/2015.
 */
public class AppException extends Exception{
    private String message = null;

    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
        this.message = message;
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
