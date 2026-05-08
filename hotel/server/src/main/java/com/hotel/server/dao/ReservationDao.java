package com.hotel.server.dao;

import com.hotel.common.entities.Reservation;
import com.hotel.server.config.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    public Reservation getById(int id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return readReservation(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры пошуку браніравання", e);
        }
    }

    public void createReservation(int guestId, int roomNumber, LocalDate reservationDate, int duration) {
        String sql = "INSERT INTO reservation(guest_id, room_number, reservation_date, duration, status) " +
                "VALUES (?, ?, ?, ?, 'pending')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guestId);
            stmt.setInt(2, roomNumber);
            stmt.setDate(3, Date.valueOf(reservationDate));
            stmt.setInt(4, duration);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры стварэнні браніравання", e);
        }
    }

    public void cancelReservation(int id) {
        checkStatus(id, false, "cancelled", "checked_out");
        update(id, "cancelled");
    }

    public void approveReservation(int id) {
        checkStatus(id, true, "pending");

        update(id, "approved");
        String roomSql = "UPDATE room r JOIN reservation res ON r.number = res.room_number " +
                "SET r.status = 'occupied' WHERE res.id = ?";
        exec(roomSql, id);
    }

    public void checkOut(int id) {
        checkStatus(id, true, "approved");
        Reservation r = getById(id);
        update(id, "checked_out");
        exec("UPDATE room SET status = 'available' WHERE number = ?", r.getRoomNumber());
    }

    public List<Reservation> getPendingReservations() {
        return query("WHERE status='pending'");
    }

    public List<Reservation> getApprovedReservations() {
        return query("WHERE status='approved'");
    }

    public List<Reservation> getAllReservations() {
        return query("");
    }

    public List<Reservation> getMyReservations(int accountId) {
        String sql = "SELECT * FROM reservation WHERE guest_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            List<Reservation> list = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(readReservation(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры чытанні", e);
        }
    }

    public List<Reservation> getMyReservationsAfterNow(int accountId) {
        String sql = "SELECT * FROM reservation WHERE guest_id = ? AND reservation_date >= CURRENT_DATE " +
                "AND status NOT IN ('cancelled','checked_out')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            List<Reservation> list = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(readReservation(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні", e);
        }
    }

    private List<Reservation> query(String where) {
        String sql = "SELECT * FROM reservation " + where;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Reservation> list = new ArrayList<>();
            while (rs.next()) list.add(readReservation(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры чытанні браніраванняў", e);
        }
    }

    private void update(int id, String status) {
        exec("UPDATE reservation SET status = '" + status + "' WHERE id = ?", id);
    }

    private void exec(String sql, int param) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры абнаўленні", e);
        }
    }

    private void checkStatus(int id, boolean mustBe, String... statuses) {
        Reservation r = getById(id);
        if (r == null) throw new RuntimeException("Браніраванне #" + id + " не знойдзена");
        String current = r.getStatus().name().toLowerCase();
        boolean found = false;
        for (String s : statuses)
            if (s.equals(current)) {
                found = true;
                break;
            }
        if (mustBe && !found)
            throw new RuntimeException("Непадыходны статус браніравання: " + current);
        if (!mustBe && found)
            throw new RuntimeException("Нельга выканаць аперацыю для статусу: " + current);
    }

    private Reservation readReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("guest_id"),
                rs.getInt("room_number"),
                rs.getDate("reservation_date").toLocalDate(),
                rs.getInt("duration"),
                Reservation.Status.valueOf(rs.getString("status").toUpperCase())
        );
    }
}
