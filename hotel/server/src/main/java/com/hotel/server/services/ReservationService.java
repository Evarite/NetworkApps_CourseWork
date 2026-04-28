package com.hotel.server.services;

import com.hotel.common.entities.Reservation;
import com.hotel.server.dao.ReservationDao;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final ReservationDao reservationDao = new ReservationDao();

    public void createReservation(int guestId, int roomNumber, LocalDate reservationDate, int duration) {
        if (reservationDate == null)
            throw new RuntimeException("Дата заезду пакінутая пустой");
        if (reservationDate.isBefore(LocalDate.now()))
            throw new RuntimeException("Дата заезду не можа быць у мінулым");
        if (duration <= 0)
            throw new RuntimeException("Працягласць браніравання павінна быць больш за 0");
        if (duration > 365)
            throw new RuntimeException("Працягласць браніравання не можа перавышаць 365 дзён");

        reservationDao.createReservation(guestId, roomNumber, reservationDate, duration);
    }

    public void cancelReservation(int reservationId) {
        reservationDao.cancelReservation(reservationId);
    }

    public void approveReservation(int reservationId) {
        reservationDao.approveReservation(reservationId);
    }

    public void checkOut(int reservationId) {
        reservationDao.checkOut(reservationId);
    }

    public List<Reservation> getPendingReservations() {
        return reservationDao.getPendingReservations();
    }

    public List<Reservation> getApprovedReservations() {
        return reservationDao.getApprovedReservations();
    }

    public List<Reservation> getMyReservationsAfterNow(int accountId) {
        var reservations = reservationDao.getMyReservationsAfterNow(accountId);
        if (reservations.isEmpty())
            throw new RuntimeException("Няма актыўных браніраванняў");
        return reservations;
    }
}
