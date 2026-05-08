package com.hotel.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ReservationRequest implements Serializable {
    private int guestId;
    private int roomNumber;
    private LocalDate reservationDate;
    private int duration;

    public ReservationRequest() {
    }

    public ReservationRequest(int guestId, int roomNumber, LocalDate reservationDate, int duration) {
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.reservationDate = reservationDate;
        this.duration = duration;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public int getDuration() {
        return duration;
    }
}
