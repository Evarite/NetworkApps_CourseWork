package com.hotel.common.dto;

import java.io.Serializable;

public record LoginRequest(String email, String password) implements Serializable {
    public LoginRequest() {
        this(null, null);
    }
}
