package com.hotel.server.controllers;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

public class RoomController {
    public Response getAllRooms(Request request) {
        return new Response(true, "[У распрацоўцы] Усе пакоі", null);
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
