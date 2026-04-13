package com.hotel.server.dao;

import com.hotel.common.entities.Guest;
import com.hotel.server.config.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestDao {
    public List<Guest> getAllGuests() {
        String sql = "SELECT * FROM guest";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            List<Guest> guests = new ArrayList<>();

            while(rs.next()) {
                guests.add(readGuest(rs));
            }

            return guests;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання гасцей", e);
        }
    }

    public List<Guest> getAllGuestsWithReservations() {
        String sql = "SELECT * FROM guest WHERE account_id IN (SELECT guest_id FROM reservation)";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            List<Guest> guests = new ArrayList<>();

            while(rs.next()) {
                guests.add(readGuest(rs));
            }

            return guests;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання гасцей", e);
        }
    }

    private Guest readGuest(ResultSet rs) throws SQLException {
        return new Guest(rs.getInt("account_id"),
                rs.getFloat("rating"),
                rs.getInt("reservations_amount"),
                rs.getInt("ratings_amount"));
    }
}
