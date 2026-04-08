package com.hotel.server.controllers;

import com.hotel.common.network.Response;
import com.hotel.common.network.Request;

public class UserController {
    public Response login(Request request) {
        return new Response(true, "Login", null);
    }

    public Response register(Request request) {
        return new Response(true, "Register", null);
    }
}
