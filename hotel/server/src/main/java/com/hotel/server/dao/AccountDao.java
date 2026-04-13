package com.hotel.server.dao;

import com.hotel.common.entities.Account;
import com.hotel.server.config.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Date;

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
            throw new RuntimeException("Памылка падчас пошука акаўнту па email", e);
        }
    }

    public Account login(String email, String password) {
        Account account = findByEmail(email);

        if(account == null)
            return null;
        if(!BCrypt.checkpw(password, account.getPassword()))
            return null;
        return account;
    }

    public Account register(String email, String firstName, String lastName, String password,
                            Date birthDate) {
        if(findByEmail(email) != null) {
            throw new RuntimeException("Email ужо заняты");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO account (email, first_name, last_name, password, birth_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, hashedPassword);
            stmt.setDate(5, new java.sql.Date(birthDate.getTime()));

            stmt.executeUpdate();

            try(ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next())
                    throw new RuntimeException("Не атрымалася атрымаць ID новага акаўнту");

                int newId = keys.getInt(1);

                return new Account(newId, email, firstName, lastName, hashedPassword, birthDate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Account readAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getDate("birth_date")
        );
    }
}
