package com.hotel.server.services;

import com.hotel.common.entities.Reservation;
import com.hotel.common.network.Response;
import com.hotel.server.dao.ReservationDao;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final ReservationDao reservationDao = new ReservationDao();

    public void createReservation(int guestId, int roomNumber, LocalDate reservationDate, int duration) {
        //guestId cannot be incorrect
        //roomNumber is chosen by GUI, always correct
        if(reservationDate == null) {
            throw new RuntimeException("Дата заезду пакінутая пустой");
        }
        if(reservationDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Няправільная дата заезду");
        }
        if(duration <= 0) {
            throw new RuntimeException("Няправільная працягласць браніравання");
        }

        reservationDao.createReservation(guestId, roomNumber, reservationDate, duration);
    }

    public List<Reservation> getMyReservationsAfterNow(int accountId) {
        var reservations = reservationDao.getMyReservationsAfterNow(accountId);
        if(reservations.isEmpty())
            throw new RuntimeException("Няма непачатых браніраванняў");
        return reservations;
    }
}
