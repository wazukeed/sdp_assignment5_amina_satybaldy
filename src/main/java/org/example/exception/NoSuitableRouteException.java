package org.example.exception;

public class NoSuitableRouteException extends RuntimeException {

    public NoSuitableRouteException(String message) {
        super(message);
    }
}