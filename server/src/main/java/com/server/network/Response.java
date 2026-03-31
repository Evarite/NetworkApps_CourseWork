package com.server.network;

import java.io.Serializable;

public class Response implements Serializable {
    private final boolean success;
    private final String message;
    private final String data;

    public Response(boolean success, String message, String data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
