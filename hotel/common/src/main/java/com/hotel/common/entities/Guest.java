package com.hotel.common.entities;

import java.io.Serializable;

public class Guest implements Serializable {
    private int accountId;
    private float rating;
    private int reservationsCount;

    public Guest() {}

    public Guest(int accountId, float rating, int reservationsCount) {
        this.accountId = accountId;
        this.rating = rating;
        this.reservationsCount = reservationsCount;
    }

    public int getAccountId()        { return accountId; }
    public float getRating()          { return rating; }
    public int getReservationsCount() { return reservationsCount; }

    public void setAccountId(int accountId)               { this.accountId = accountId; }
    public void setRating(float rating)                   { this.rating = rating; }
    public void setReservationsCount(int reservationsCount){ this.reservationsCount = reservationsCount; }

    /** Аварэджны рэйтынг ад 0.0 да 5.0 */
    public float getAverageRating() {
        if (reservationsCount == 0) return 0f;
        return Math.min(5f, rating / reservationsCount);
    }
}
