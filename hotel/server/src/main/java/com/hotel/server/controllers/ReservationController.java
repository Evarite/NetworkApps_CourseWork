package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.ReservationService;

public class ReservationController {
    private final ReservationService reservationService = new ReservationService();
    private final ObjectMapper mapper = new ObjectMapper();

    public ReservationController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response createReservation(Request request) {
        try {
            ReservationRequest req = mapper.readValue(request.getData(), ReservationRequest.class);
            reservationService.createReservation(
                    req.getGuestId(), req.getRoomNumber(),
                    req.getReservationDate(), req.getDuration()
            );
            return new Response(true, "Браніраванне створана. Чакаецца пацверджанне.", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response cancelReservation(Request request) {
        try {
            int reservationId = mapper.readValue(request.getData(), Integer.class);
            reservationService.cancelReservation(reservationId);
            return new Response(true, "Браніраванне паспяхова скасавана", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response approveReservation(Request request) {
        try {
            int reservationId = mapper.readValue(request.getData(), Integer.class);
            reservationService.approveReservation(reservationId);
            return new Response(true, "Браніраванне паспяхова зацверджана", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response checkOut(Request request) {
        try {
            int reservationId = mapper.readValue(request.getData(), Integer.class);
            reservationService.checkOut(reservationId);
            return new Response(true, "Госць паспяхова выселены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getMyReservations(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var reservations = reservationService.getPendingReservations();
            var dao = new com.hotel.server.dao.ReservationDao();
            var myReservations = dao.getMyReservations(accountId);
            String json = mapper.writeValueAsString(myReservations);
            return new Response(true, "Атрымана браніраванняў: " + myReservations.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getAllReservations(Request request) {
        try {
            var dao = new com.hotel.server.dao.ReservationDao();
            var allReservations = dao.getAllReservations();
            String json = mapper.writeValueAsString(allReservations);
            return new Response(true, "Атрымана браніраванняў: " + allReservations.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getPendingReservations(Request request) {
        try {
            var pending = reservationService.getPendingReservations();
            String json = mapper.writeValueAsString(pending);
            return new Response(true, "Чакаюць зацвярджэння: " + pending.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getApprovedReservations(Request request) {
        try {
            var approved = reservationService.getApprovedReservations();
            String json = mapper.writeValueAsString(approved);
            return new Response(true, "Зацверджаных: " + approved.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getMyReservationsAfterNow(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var reservations = reservationService.getMyReservationsAfterNow(accountId);
            String json = mapper.writeValueAsString(reservations);
            return new Response(true, "Актыўных браніраванняў: " + reservations.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }
}
