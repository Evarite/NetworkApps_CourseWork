package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.GuestDao;
import com.hotel.server.exceptions.ResponseException;

public class GuestController {
    private final GuestDao guestDao = new GuestDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public GuestController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response getAllGuests(Request request) {
        try {
            var guests = guestDao.getAllGuests();
            return new Response(true, "OK", mapper.writeValueAsString(guests));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getAllGuestsWithReservations(Request request) {
        try {
            var guests = guestDao.getAllGuestsWithReservations();
            return new Response(true, "OK", mapper.writeValueAsString(guests));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }
}
