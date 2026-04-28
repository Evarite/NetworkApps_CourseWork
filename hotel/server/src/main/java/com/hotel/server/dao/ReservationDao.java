package com.hotel.server.dao;

import com.hotel.common.entities.Reservation;
import com.hotel.server.config.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

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
            throw new RuntimeException("Памылка падчас стварэння браніравання", e);
        }
    }

    public void cancelReservation(int id) {
        String checkSql = "SELECT status FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, id);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next())
                    throw new RuntimeException("Браніраванне з ID " + id + " не знойдзена");
                String status = rs.getString("status");
                if ("checked_out".equals(status))
                    throw new RuntimeException("Нельга скасаваць браніраванне: госць ужо выселены");
                if ("cancelled".equals(status))
                    throw new RuntimeException("Браніраванне ўжо скасавана");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры праверцы браніравання", e);
        }

        String sql = "UPDATE reservation SET status = 'cancelled' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас скасавання браніравання", e);
        }
    }

    public void approveReservation(int id) {
        String checkSql = "SELECT status, room_number FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, id);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next())
                    throw new RuntimeException("Браніраванне з ID " + id + " не знойдзена");
                if (!"pending".equals(rs.getString("status")))
                    throw new RuntimeException("Браніраванне ўжо апрацавана");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры праверцы браніравання", e);
        }

        String sql = "UPDATE reservation SET status = 'approved' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас зацвярджэння браніравання", e);
        }

        String roomSql = "UPDATE room r " +
                "JOIN reservation res ON r.number = res.room_number " +
                "SET r.status = 'occupied' WHERE res.id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(roomSql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры змене статусу пакоя", e);
        }
    }

    public void checkOut(int reservationId) {
        String checkSql = "SELECT status, room_number FROM reservation WHERE id = ?";
        int roomNumber;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, reservationId);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next())
                    throw new RuntimeException("Браніраванне з ID " + reservationId + " не знойдзена");
                String status = rs.getString("status");
                if (!"approved".equals(status))
                    throw new RuntimeException("Выселіць можна толькі зацверджаныя браніраванні (статус: " + status + ")");
                roomNumber = rs.getInt("room_number");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры праверцы браніравання", e);
        }

        String sql = "UPDATE reservation SET status = 'checked_out' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас выселення", e);
        }

        String roomSql = "UPDATE room SET status = 'available' WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(roomSql)) {
            stmt.setInt(1, roomNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры змене статусу пакоя", e);
        }
    }

    public List<Reservation> getPendingReservations() {
        String sql = "SELECT * FROM reservation WHERE status = 'pending'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Reservation> list = new ArrayList<>();
            while (rs.next()) list.add(readReservation(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    public List<Reservation> getApprovedReservations() {
        String sql = "SELECT * FROM reservation WHERE status = 'approved'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Reservation> list = new ArrayList<>();
            while (rs.next()) list.add(readReservation(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    public List<Reservation> getMyReservations(int accountId) {
        String sql = "SELECT * FROM reservation WHERE guest_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            List<Reservation> reservations = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) reservations.add(readReservation(rs));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    public List<Reservation> getAllReservations() {
        String sql = "SELECT * FROM reservation";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) reservations.add(readReservation(rs));
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    public List<Reservation> getMyReservationsAfterNow(int accountId) {
        String sql = "SELECT * FROM reservation WHERE guest_id = ? AND reservation_date >= CURRENT_DATE " +
                "AND status NOT IN ('cancelled', 'checked_out')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            List<Reservation> reservations = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) reservations.add(readReservation(rs));
            }
            return reservations;
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас чытання браніраванняў", e);
        }
    }

    private Reservation readReservation(ResultSet rs) throws SQLException {
        String statusStr = rs.getString("status");
        Reservation.Status status = Reservation.Status.valueOf(statusStr.toUpperCase());
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("guest_id"),
                rs.getInt("room_number"),
                rs.getDate("reservation_date").toLocalDate(),
                rs.getInt("duration"),
                status
        );
    }
}
