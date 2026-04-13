package com.hotel.server.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }

    public String getStack() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        return pw.toString();
    }
}
