package com.hotel.common.dto;

import java.io.Serializable;
import java.util.Date;

public class ReservationRequest implements Serializable {
    private int guestId;
    private int roomNumber;
    private Date reservationDate;
    private int duration;

    public ReservationRequest() {}

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
}
