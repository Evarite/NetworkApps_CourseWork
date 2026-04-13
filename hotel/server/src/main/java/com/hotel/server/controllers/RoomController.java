package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.RoomDao;
import com.hotel.server.exceptions.ResponseException;

public class RoomController {
    private final RoomDao roomDao = new RoomDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllRooms(Request request) {
        var rooms = roomDao.getAllRooms();

        try {
            String json = mapper.writeValueAsString(rooms);
            return new Response(true, "Атрымана нумароў: " + rooms.size(), json);
        } catch(Exception e) {
            throw new ResponseException("Памылка падчас стварэння JSON файла: ");
        }
    }

    public Response addRoom(Request request) {
        return new Response(true, "[У распрацоўцы] Дадаць пакой", null);
    }

    public Response closeRoom(Request request) {
        return new Response(true, "[У распрацоўцы] Зачыніць пакой", null);
    }

    public Response deleteRoom(Request request) {
        return new Response(true, "[У распрацоўцы] Выдаліць пакой", null);
    }

    public Response updateRoom(Request request) {
        return new Response(true, "[У распрацоўцы] Аднавіць пакой", null);
    }
}
