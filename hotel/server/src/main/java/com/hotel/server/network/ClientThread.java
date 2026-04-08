package com.hotel.server.network;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

import com.hotel.server.controllers.UserController;
import com.hotel.server.controllers.RoomController;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientThread.class);
    private final Socket socket;
    private final UserController userController = new UserController();
    private final RoomController roomController = new RoomController();

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Запушчаны паток для кліента: " + socket);

        try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            while (true) {
                Request request = (Request) in.readObject();
                logger.info("Аперацыя: " + request.getOperation());

                Response response = processRequest(request);

                out.writeObject(response);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Кліент адключыўся: " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Response processRequest(Request request) {
        return switch (request.getOperation()) {
            case LOGIN -> userController.login(request);
            case REGISTER -> userController.register(request);

            case ADD_ROOM -> roomController.addRoom(request);
            case CLOSE_ROOM -> roomController.closeRoom(request);
            case DELETE_ROOM -> roomController.deleteRoom(request);
            case UPDATE_ROOM -> roomController.updateRoom(request);
            case GET_ALL_ROOMS -> roomController.getAllRooms(request);

            case CREATE_RESERVATION -> new Response(true, "На распрацоўцы", null);
            case CANCEL_RESERVATION -> new Response(true, "На распрацоўцы", null);
            case APPROVE_RESERVATION -> new Response(true, "На распрацоўцы", null);
            case GET_ALL_RESERVATIONS -> new Response(true, "На распрацоўцы", null);
            case GET_MY_RESERVATIONS -> new Response(true, "На распрацоўцы", null);

            case GET_AVAILABLE_ROOMS -> new Response(true, "На распрацоўцы", null);

            case GET_ALL_EMPLOYEES -> new Response(true, "На распрацоўцы", null);
            case HIRE_EMPLOYEE -> new Response(true, "На распрацоўцы", null);
            case FIRE_EMPLOYEE -> new Response(true, "На распрацоўцы", null);
            case CHANGE_ROLE -> new Response(true, "На распрацоўцы", null);

            case UPDATE_PROFILE -> new Response(true, "На распрацоўцы", null);
            case CHECK_PASSWORD -> new Response(true, "На распрацоўцы", null);

            case DISCONNECT -> new Response(true, "На распрацоўцы", null);

            default -> new Response(false, "Невядомая апэрацыя", null);
        };
    }
}
