package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.dto.CheckOutRequest;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.GuestDao;
import com.hotel.server.dao.ReservationDao;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.ReservationService;

public class ReservationController {
    private final ReservationService reservationService = new ReservationService();
    private final ReservationDao reservationDao = new ReservationDao();
    private final GuestDao guestDao = new GuestDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public ReservationController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response createReservation(Request request) {
        try {
            ReservationRequest req = mapper.readValue(request.getData(), ReservationRequest.class);
            guestDao.ensureGuestExists(req.getGuestId());
            reservationService.createReservation(
                    req.getGuestId(), req.getRoomNumber(),
                    req.getReservationDate(), req.getDuration());
            return new Response(true, "Браніраванне создана. Чакаецца пацверджанне.", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response cancelReservation(Request request) {
        try {
            int id = mapper.readValue(request.getData(), Integer.class);
            reservationService.cancelReservation(id);
            return new Response(true, "Браніраванне паспяхова скасавана", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response approveReservation(Request request) {
        try {
            int id = mapper.readValue(request.getData(), Integer.class);
            reservationService.approveReservation(id);
            return new Response(true, "Браніраванне зацверджана", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    /**
     * Выпраўлена: checkOut прымае CheckOutRequest (ID + рэйтынг 1-5).
     * 1) Змяняе статус браніравання на checked_out, пакой вяртаецца ў available
     * 2) Дадае адзнаку да рэйтынгу госця
     */
    public Response checkOut(Request request) {
        try {
            CheckOutRequest req = mapper.readValue(request.getData(), CheckOutRequest.class);

            if (req.getRating() < 1 || req.getRating() > 5)
                throw new RuntimeException("Адзнака павінна быць ад 1 да 5");

            // Атрымліваем guestId перад выселеннем
            var reservation = reservationDao.getById(req.getReservationId());
            if (reservation == null)
                throw new RuntimeException("Браніраванне не знойдзена");

            // 1. Выселіць
            reservationService.checkOut(req.getReservationId());

            // 2. Дадаць адзнаку
            guestDao.addRating(reservation.getGuestId(), req.getRating());

            return new Response(true, "Госць паспяхова выселены. Адзнака: "
                    + req.getRating() + "/5 дададзена.", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getMyReservations(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var list = reservationDao.getMyReservations(accountId);
            return new Response(true, "OK", mapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getAllReservations(Request request) {
        try {
            var list = reservationDao.getAllReservations();
            return new Response(true, "OK", mapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getPendingReservations(Request request) {
        try {
            var list = reservationService.getPendingReservations();
            return new Response(true, "OK", mapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getApprovedReservations(Request request) {
        try {
            var list = reservationService.getApprovedReservations();
            return new Response(true, "OK", mapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response getMyReservationsAfterNow(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            var list = reservationService.getMyReservationsAfterNow(accountId);
            return new Response(true, "OK", mapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }
}
