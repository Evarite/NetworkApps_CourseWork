package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.entities.Room;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.RoomDao;
import com.hotel.server.exceptions.ResponseException;

public class RoomController {
    private final RoomDao roomDao = new RoomDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllRooms(Request request) {
        try {
            var rooms = roomDao.getAllRooms();
            String json = mapper.writeValueAsString(rooms);
            return new Response(true, "Атрымана нумароў: " + rooms.size(), json);
        } catch(Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response getAvailableRooms(Request request) {
        try {
            var rooms = roomDao.getAvailableRooms();
            String json = mapper.writeValueAsString(rooms);
            return new Response(true, "Атрымана нумароў: " + rooms.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response addRoom(Request request) {
        try {
            Room room = mapper.readValue(request.getData(), Room.class);
            roomDao.addRoom(room);
            return new Response(true, "Створана паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response closeRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomDao.closeRoom(number);
            return new Response(true, "Зачынена паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response deleteRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomDao.deleteRoom(number);
            return new Response(true, "Выдалена паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response updateRoom(Request request) {
        try {
            Room room = mapper.readValue(request.getData(), Room.class);
            roomDao.updateRoom(room);
            return new Response(true, "Створана паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response openRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomDao.openRoom(number);
            return new Response(true, "Выдалена паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }
}
