package com.hotel.server.exceptions;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }
}
