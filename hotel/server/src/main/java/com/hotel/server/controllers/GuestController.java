package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.GuestDao;
import com.hotel.server.exceptions.ResponseException;

public class GuestController {
    private final GuestDao guestDao = new GuestDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllGuests(Request request) {
        try {
            var guests = guestDao.getAllGuests();
            String json = mapper.writeValueAsString(guests);
            return new Response(true, "Атрымана гасцей: " + guests.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getAllGuestsWithReservations(Request request) {
        try {
            var guests = guestDao.getAllGuestsWithReservations();
            String json = mapper.writeValueAsString(guests);
            return new Response(true, "Атрымана гасцей: " + guests.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }
}
