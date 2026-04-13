package com.hotel.server.dao;

import com.hotel.common.entities.Reservation;
import com.hotel.server.config.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationDao {
    //Add status as approved or awaiting
    public void createReservation(int guestId, int roomNumber, Date reservationDate, int duration) {
        String sql = "INSERT INTO reservation(guest_id, room_number, reservation_date, duration)" +
                "VALUES (?, ?, ?, ?)";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guestId);
            stmt.setInt(2, roomNumber);
            stmt.setDate(3, new java.sql.Date (reservationDate.getTime()));
            stmt.setInt(4, duration);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас стварэння браніравання", e);
        }
    }

    public void cancelReservation(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас скасавання браніравання", e);
        }
    }

    public void checkOut(int id) {
        //
    }

    public List<Reservation> getMyReservations(int accountId) {
        String sql = "SELECT * FROM reservation WHERE account_id = ?";

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);

            List<Reservation> reservations = new ArrayList<>();
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    reservations.add(readReservation(rs));
            }

            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    private Reservation readReservation(ResultSet rs) throws SQLException {
        return new Reservation(rs.getInt("id"),
                rs.getInt("guest_id"),
                rs.getInt("room_number"),
                rs.getDate("reservation_date"),
                rs.getInt("duration"));
    }
}
