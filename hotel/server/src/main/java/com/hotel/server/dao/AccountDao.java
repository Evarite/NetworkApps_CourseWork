package com.hotel.server.dao;

import com.hotel.common.entities.Account;
import com.hotel.server.config.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

public class AccountDao {

    public Account findByEmail(String email) {
        String sql = "SELECT * FROM account WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return readAccount(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас пошуку акаўнта па email", e);
        }
    }

    public Account login(String email, String password) {
        Account account = findByEmail(email);

        if (account == null)
            return null;

        if (!BCrypt.checkpw(password, account.getPassword()))
            return null;

        return account;
    }

    public Account register(String email, String firstName, String lastName, String password,
                            LocalDate birthDate) {
        String sql = "INSERT INTO account (email, first_name, last_name, password_hash, birth_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, password);
            stmt.setDate(5, Date.valueOf(birthDate));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next())
                    throw new RuntimeException("Не атрымалася атрымаць ID новага акаўнта");

                int newId = keys.getInt(1);
                return new Account(newId, email, firstName, lastName, password, birthDate);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка падчас рэгістрацыі", e);
        }
    }

    public void updateAccount(int id, String newEmail, String newFirstName, String newLastName,
                              String newPassword) {
        String sql = "UPDATE account SET email = ?, first_name = ?, last_name = ?, password_hash = ? " +
                "WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, newFirstName);
            stmt.setString(3, newLastName);
            stmt.setString(4, newPassword);
            stmt.setInt(5, id);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас абнаўлення дадзеных акаўнта", e);
        }
    }

    private Account readAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password_hash"),
                rs.getDate("birth_date").toLocalDate()
        );
    }
}
