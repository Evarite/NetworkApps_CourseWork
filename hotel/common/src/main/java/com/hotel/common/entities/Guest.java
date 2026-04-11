package com.hotel.common.entities;

import java.io.Serializable;

public class Guest implements Serializable {
    private final int accountId;
    private float rating;
    private int reservationsAmount;
    private int ratingsAmount;

    public Guest(int accountId, float rating, int reservationsAmount, int ratingsAmount) {
        this.accountId = accountId;
        this.rating = rating;
        this.reservationsAmount = reservationsAmount;
        this.ratingsAmount = ratingsAmount;
    }

    public int getAccountId() {
        return accountId;
    }
    public float getRating() {
        return rating;
    }
    public int getReservationsAmount() {
        return reservationsAmount;
    }
    public int getRatingsAmount() {
        return ratingsAmount;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public void setRatingsAmount(int ratingsAmount) {
        this.ratingsAmount = ratingsAmount;
    }
    public void setReservationsAmount(int reservationsAmount) {
        this.reservationsAmount = reservationsAmount;
    }
}
