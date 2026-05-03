package com.hotel.common.dto;

import java.io.Serializable;

public class CheckOutRequest implements Serializable {
    private int reservationId;
    private int rating; // 1-5

    public CheckOutRequest() {}

    public CheckOutRequest(int reservationId, int rating) {
        this.reservationId = reservationId;
        this.rating = rating;
    }

    public int getReservationId() { return reservationId; }
    public int getRating()        { return rating; }
    public void setReservationId(int v) { this.reservationId = v; }
    public void setRating(int v)        { this.rating = v; }
}
