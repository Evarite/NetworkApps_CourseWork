package com.hotel.server.dao;

import com.hotel.common.entities.Employee;
import com.hotel.server.config.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next())
                employees.add(readEmployee(rs));

            return employees;
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні супрацоўнікаў", e);
        }
    }

    public void hireEmployee(Employee employee) {
        String sql = "INSERT INTO employee VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employee.getAccountId());
            stmt.setString(2, employee.getPosition().name().toLowerCase());
            stmt.setFloat(3, employee.getSalary());
            stmt.setDate(4, new java.sql.Date(employee.getHireDate().getTime()));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры ўладкаванні супрацоўніка", e);
        }
    }

    public void fireEmployee(int accountId) {
        String sql = "DELETE FROM employee WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры звальненні супрацоўніка", e);
        }
    }

    public void changeRole(int accountId, Employee.Position newPosition) {
        String sql = "UPDATE employee SET position = ? WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPosition.name().toLowerCase());
            stmt.setInt(2, accountId);
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры змене пасады супрацоўніка", e);
        }
    }

    private Employee readEmployee(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("account_id"),
                Employee.Position.valueOf(rs.getString("position").toUpperCase()),
                rs.getFloat("salary"),
                rs.getDate("date"));
    }
}
