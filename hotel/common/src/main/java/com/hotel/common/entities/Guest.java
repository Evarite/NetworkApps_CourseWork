package com.hotel.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Guest implements Serializable {

    private int accountId;
    private float rating;
    private int reservationsAmount;
    private int ratingsAmount;

    public Guest() {
    }

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

    public void setAccountId(int v) {
        this.accountId = v;
    }

    public void setRating(float v) {
        this.rating = v;
    }

    public void setReservationsAmount(int v) {
        this.reservationsAmount = v;
    }

    public void setRatingsAmount(int v) {
        this.ratingsAmount = v;
    }

    @JsonIgnore
    public float getAverageRating() {
        if (ratingsAmount == 0) return 0f;
        return Math.min(5f, rating / ratingsAmount);
    }
}

