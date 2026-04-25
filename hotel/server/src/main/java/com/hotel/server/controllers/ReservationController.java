package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.ReservationDao;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.ReservationService;

public class ReservationController {
    private final ReservationService reservationService = new ReservationService();
    private final ReservationDao reservationDao = new ReservationDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response createReservation(Request request) {
        try {
            ReservationRequest reservationRequest = mapper.readValue(request.getData(),
                    ReservationRequest.class);
            reservationService.createReservation(reservationRequest.getGuestId(),
                    reservationRequest.getRoomNumber(), reservationRequest.getReservationDate(),
                    reservationRequest.getDuration());
            return new Response(true, "Браніраванне створанае паспяхова." +
                    "Чакаецца пацверджанне.", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response cancelReservation(Request request) {
        try {
            int id = mapper.readValue(request.getData(), Integer.class);
            reservationDao.cancelReservation(id);
            return new Response(true, "Браніраванне скасаванае паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response checkOut(Request request) {
        return new Response(true, "У распрацоўцы", null);
    }

    public Response getMyReservations(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var reservations = reservationDao.getMyReservations(accountId);
            String json = mapper.writeValueAsString(reservations);
            return new Response(true, "Атрымана браніраванняў: " + reservations.size(),
                    json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getAllReservations(Request request) {
        try {
            var reservations = reservationDao.getAllReservations();
            String json = mapper.writeValueAsString(reservations);
            return new Response(true, "Атрымана браніраванняў: " + reservations.size(),
                    json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response approveReservation(Request request) {
        return new Response(true, "У распрацоўцы", null);
    }

    public Response getMyReservationsAfterNow(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var reservations = reservationService.getMyReservationsAfterNow(accountId);
            String json = mapper.writeValueAsString(reservations);
            return new Response(true, "Атрымана пакояў: " + reservations.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }
}
