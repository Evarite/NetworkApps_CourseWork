package com.server.network;

import com.server.enums.Operation;
import java.io.Serializable;

public class Request implements Serializable {
    private final Operation operation;
    private final String data;

    public Request(Operation operation, String data) {
        this.operation = operation;
        this.data = data;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getData() {
        return data;
    }
}
