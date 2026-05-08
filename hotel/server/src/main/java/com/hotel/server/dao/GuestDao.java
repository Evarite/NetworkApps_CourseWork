package com.hotel.server.dao;

import com.hotel.common.entities.Guest;
import com.hotel.server.config.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDao {

    public Guest findByAccountId(int accountId) {
        String sql = "SELECT * FROM guest WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return read(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры пошуку госця", e);
        }
    }

    public void ensureGuestExists(int accountId) {
        if (findByAccountId(accountId) != null) return;
        String sql = "INSERT IGNORE INTO guest(account_id, rating, reservations_amount, ratings_amount) " +
                "VALUES (?, 0, 0, 0)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры стварэнні запісу госця", e);
        }
    }

    public void addRating(int accountId, int stars) {
        if (stars < 1 || stars > 5)
            throw new RuntimeException("Адзнака павінна быць ад 1 да 5");

        ensureGuestExists(accountId);

        String sql = "UPDATE guest " +
                "SET rating = rating + ?, ratings_amount = ratings_amount + 1 " +
                "WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stars);
            stmt.setInt(2, accountId);
            int rows = stmt.executeUpdate();
            if (rows == 0)
                throw new RuntimeException("Госць з ID " + accountId + " не знойдзены");
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры абнаўленні рэйтынгу", e);
        }
    }

    public void incrementReservations(int accountId) {
        ensureGuestExists(accountId);
        String sql = "UPDATE guest SET reservations_amount = reservations_amount + 1 WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры абнаўленні лічыльніка браніраванняў", e);
        }
    }

    public List<Guest> getAllGuests() {
        return query("SELECT * FROM guest");
    }

    public List<Guest> getAllGuestsWithReservations() {
        String sql = "SELECT g.* FROM guest g " +
                "WHERE EXISTS (" +
                "  SELECT 1 FROM reservation r " +
                "  WHERE r.guest_id = g.account_id AND r.status NOT IN ('cancelled')" +
                ")";
        return query(sql);
    }

    private List<Guest> query(String sql) {
        List<Guest> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(read(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры чытанні гасцей", e);
        }
    }

    private Guest read(ResultSet rs) throws SQLException {
        return new Guest(
                rs.getInt("account_id"),
                rs.getFloat("rating"),
                rs.getInt("reservations_amount"),
                rs.getInt("ratings_amount")
        );
    }
}
