package com.hotel.common.entities;

import java.io.Serializable;
import java.time.LocalDate;

public class Reservation implements Serializable{
    private final int id;
    private final int guestId;
    private int roomNumber;
    private LocalDate reservationDate;
    private int duration;

    public Reservation(int id, int guestId, int roomNumber, LocalDate reservationDate, int duration) {
        this.id = id;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.reservationDate = reservationDate;
        this.duration = duration;
    }

    public int getId() {
        return id;
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

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
