package com.hotel.server.controllers;

import com.hotel.common.network.Response;
import com.hotel.common.network.Request;
import com.hotel.server.dao.UserDao;

public class UserController {
    private final UserDao userDao = new UserDao();

    public Response login(Request request) {
        return new Response(true, "Login", null);
    }

    public Response register(Request request) {
        return new Response(true, "Register", null);
    }
}
