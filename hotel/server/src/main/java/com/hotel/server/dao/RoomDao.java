package com.hotel.server.dao;

import com.hotel.server.config.DatabaseManager;
import com.hotel.common.entities.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class RoomDao {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(readRoom(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні нумароў", e);
        }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'available'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(readRoom(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні нумароў", e);
        }
        return rooms;
    }

    public void addRoom(Room room) {
        if (roomExists(room.getNumber()))
            throw new RuntimeException("Пакой з нумарам " + room.getNumber() + " ужо існуе");

        String sql = "INSERT INTO room VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getNumber());
            stmt.setInt(2, room.getFloor());
            stmt.setString(3, typeToDb(room.getType()));
            stmt.setString(4, room.getCapacity().name().toLowerCase());
            stmt.setString(5, room.getDescription());
            stmt.setString(6, room.getStatus().name().toLowerCase());
            stmt.setFloat(7, room.getPrice());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры стварэнні новага пакоя", e);
        }
    }

    public void updateRoom(Room room) {
        if (!roomExists(room.getNumber()))
            throw new RuntimeException("Пакой з нумарам " + room.getNumber() + " не знойдзены");

        String sql = "UPDATE room SET floor = ?, type = ?, capacity = ?, description = ?, " +
                "status = ?, price = ? WHERE number = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getFloor());
            stmt.setString(2, typeToDb(room.getType()));
            stmt.setString(3, room.getCapacity().name().toLowerCase());
            stmt.setString(4, room.getDescription());
            stmt.setString(5, room.getStatus().name().toLowerCase());
            stmt.setFloat(6, room.getPrice());
            stmt.setInt(7, room.getNumber());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры абнаўленні пакоя", e);
        }
    }

    public void deleteRoom(int number) {
        String checkSql = "SELECT status FROM room WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, number);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next())
                    throw new RuntimeException("Пакой з нумарам " + number + " не знойдзены");
                if ("occupied".equals(rs.getString("status")))
                    throw new RuntimeException("Нельга выдаліць занятны пакой");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры праверцы пакоя", e);
        }

        String sql = "DELETE FROM room WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры выдаленні пакоя", e);
        }
    }

    public void closeRoom(int number) {
        String checkSql = "SELECT status FROM room WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, number);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next())
                    throw new RuntimeException("Пакой з нумарам " + number + " не знойдзены");
                if ("occupied".equals(rs.getString("status")))
                    throw new RuntimeException("Нельга зачыніць занятны пакой");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Памылка пры праверцы пакоя", e);
        }

        String sql = "UPDATE room SET status = 'maintenance' WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры зачыненні пакоя", e);
        }
    }

    public void openRoom(int number) {
        if (!roomExists(number))
            throw new RuntimeException("Пакой з нумарам " + number + " не знойдзены");

        String sql = "UPDATE room SET status = 'available' WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры адкрыцці пакоя", e);
        }
    }

    private boolean roomExists(int number) {
        String sql = "SELECT 1 FROM room WHERE number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String typeToDb(Room.Type type) {
        return switch (type) {
            case JUNIOR_SUITE -> "junior suite";
            default -> type.name().toLowerCase();
        };
    }

    private Room.Type typeFromDb(String value) {
        if ("junior suite".equals(value)) return Room.Type.JUNIOR_SUITE;
        return Room.Type.valueOf(value.toUpperCase());
    }

    private Room readRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("number"),
                rs.getInt("floor"),
                typeFromDb(rs.getString("type")),
                Room.Capacity.valueOf(rs.getString("capacity").toUpperCase()),
                rs.getString("description"),
                Room.Status.valueOf(rs.getString("status").toUpperCase()),
                rs.getFloat("price")
        );
    }
}
