package com.enset.bdcc.commonapi.exceptions;


public class BallanceInsufficientException extends RuntimeException {
    public BallanceInsufficientException(String message) {
        super(message);
    }
}
