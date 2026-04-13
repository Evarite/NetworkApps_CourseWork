package com.hotel.server.dao;

import com.hotel.server.config.DatabaseManager;
import com.hotel.common.entities.Room;
import com.hotel.server.exceptions.ResponseException;

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

        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while(rs.next()) {
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
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                rooms.add(readRoom(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні нумароў", e);
        }

        return rooms;
    }

    public void addRoom(Room room) {
        String sql = "INSERT INTO room VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getNumber());
            stmt.setInt(2, room.getFloor());
            stmt.setString(3, room.getType().name().toLowerCase());
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
        String sql = "UPDATE room SET floor = ?, type = ?, capacity = ?, description = ?, " +
                "status = ?, price = ? WHERE number = ?";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getFloor());
            stmt.setString(2, room.getType().name().toLowerCase());
            stmt.setString(3, room.getCapacity().name().toLowerCase());
            stmt.setString(4, room.getDescription());
            stmt.setString(5, room.getStatus().name().toLowerCase());
            stmt.setFloat(6, room.getPrice());
            stmt.setInt(7, room.getNumber());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры аднаўленні пакоя", e);
        }
    }

    public void deleteRoom(int number) { //Add a check, of whether the room is occupied
        String sql = "DELETE FROM room WHERE number = ?";

        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры выдаленні пакоя", e);
        }
    }

    public void closeRoom(int number) { //Add a check, of whether the room is occupied
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
        String sql = "UPDATE room SET status = 'available' WHERE number = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры зачыненні пакоя", e);
        }
    }

    private Room readRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("number"),
                rs.getInt("floor"),
                Room.Type.valueOf(rs.getString("type").toUpperCase()),
                Room.Capacity.valueOf(rs.getString("capacity").toUpperCase()),
                rs.getString("description"),
                Room.Status.valueOf(rs.getString("status").toUpperCase()),
                rs.getFloat("price")
        );
    }
}
