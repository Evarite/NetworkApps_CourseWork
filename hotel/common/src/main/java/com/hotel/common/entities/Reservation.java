package com.hotel.common.entities;

import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable{
    private final int id;
    private final int guestId;
    private int roomNumber;
    private Date reservationDate;
    private int duration;

    public Reservation(int id, int guestId, int roomNumber, Date reservationDate, int duration) {
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
    public Date getReservationDate() {
        return reservationDate;
    }
    public int getDuration() {
        return duration;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
