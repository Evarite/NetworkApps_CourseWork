package com.hotel.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private final boolean success;
    private final String message;
    private final Object data;

    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
