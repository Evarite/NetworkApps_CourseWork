package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.entities.Room;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.RoomService;

public class RoomController {

    private final RoomService  roomService = new RoomService();
    private final ObjectMapper mapper      = new ObjectMapper();

    public RoomController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response getAllRooms(Request request) {
        try {
            var rooms = roomService.getAllRooms();
            return new Response(true, "OK", mapper.writeValueAsString(rooms));
        } catch (Exception e) {
            throw new ResponseException("Памылка пры чытанні нумароў: " + e.getMessage());
        }
    }

    public Response getAvailableRooms(Request request) {
        try {
            var rooms = roomService.getAvailableRooms();
            return new Response(true, "OK", mapper.writeValueAsString(rooms));
        } catch (Exception e) {
            throw new ResponseException("Памылка: " + e.getMessage());
        }
    }

    public Response addRoom(Request request) {
        try {
            Room room = mapper.readValue(request.getData(), Room.class);
            roomService.addRoom(room);
            return new Response(true, "Нумар #" + room.getNumber() + " паспяхова дададзены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры дабаўленні нумара: " + e.getMessage());
        }
    }

    public Response updateRoom(Request request) {
        try {
            Room room = mapper.readValue(request.getData(), Room.class);
            roomService.updateRoom(room);
            return new Response(true, "Нумар #" + room.getNumber() + " паспяхова абноўлены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры абнаўленні нумара: " + e.getMessage());
        }
    }

    public Response deleteRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomService.deleteRoom(number);
            return new Response(true, "Нумар #" + number + " паспяхова выдалены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры выдаленні нумара: " + e.getMessage());
        }
    }

    public Response closeRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomService.closeRoom(number);
            return new Response(true, "Нумар #" + number + " зачынены на тэхабслугоўванне", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры зачыненні нумара: " + e.getMessage());
        }
    }

    public Response openRoom(Request request) {
        try {
            int number = mapper.readValue(request.getData(), Integer.class);
            roomService.openRoom(number);
            return new Response(true, "Нумар #" + number + " адчынены і зноў даступны", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры адкрыцці нумара: " + e.getMessage());
        }
    }
}
