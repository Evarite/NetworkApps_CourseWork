package com.hotel.server.dao;

import com.hotel.server.config.DatabaseManager;
import com.hotel.common.entities.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class RoomDao {
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();

        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM room");
                ResultSet rs = stmt.executeQuery()
        ) {
            while(rs.next()) {
                Room room = new Room(
                        rs.getInt("number"),
                        rs.getInt("floor"),
                        Room.Type.valueOf(rs.getString("type").toUpperCase()),
                        Room.Capacity.valueOf(rs.getString("capacity").toUpperCase()),
                        rs.getString("description"),
                        Room.Status.valueOf(rs.getString("status").toUpperCase()),
                        rs.getFloat("price")
                );

                rooms.add(room);
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка пры чытанні нумароў", e);
        }

        return rooms;
    }
}
