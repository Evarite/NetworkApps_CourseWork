package com.hotel.server.controllers;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.RoomDao;

public class RoomController {
    private final RoomDao roomDao = new RoomDao();

    public Response getAllRooms(Request request) {
        var rooms = roomDao.getAllRooms();
        return new Response(true, "Атрымана нумароў: " + rooms.size(), rooms.toString());
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
