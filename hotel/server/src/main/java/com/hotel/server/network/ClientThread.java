package com.hotel.server.network;

import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

import com.hotel.server.controllers.*;

import com.hotel.server.exceptions.ResponseException;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientThread.class);
    private final Socket socket;
    private final UserController userController = new UserController();
    private final RoomController roomController = new RoomController();
    private final GuestController guestController = new GuestController();
    private final EmployeeController employeeController = new EmployeeController();
    private final ReservationController reservationController = new ReservationController();

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
            boolean running = true;
            while (running) {
                Request request = (Request) in.readObject();
                logger.info("Аперацыя: " + request.getOperation());

                Response response = processRequest(request);

                out.writeObject(response);
                out.flush();

                if(request.getOperation() == Operation.DISCONNECT) {
                    logger.info("Кліент адлучыўся");
                    running = false;
                }
            }

        } catch (Exception e) {
            logger.error("Кліент адлучыўся: " + socket);
            System.out.println("Кліент адлучыўся: " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case LOGIN -> userController.login(request);
                case REGISTER -> userController.register(request);

                case ADD_ROOM -> roomController.addRoom(request);
                case CLOSE_ROOM -> roomController.closeRoom(request);
                case DELETE_ROOM -> roomController.deleteRoom(request);
                case UPDATE_ROOM -> roomController.updateRoom(request);
                case GET_ALL_ROOMS -> roomController.getAllRooms(request);

                case CREATE_RESERVATION -> reservationController.createReservation(request);
                case CANCEL_RESERVATION -> reservationController.cancelReservation(request);
                case GET_MY_RESERVATIONS -> reservationController.getMyReservations(request);
                case GET_ALL_RESERVATIONS -> reservationController.getAllReservation(request);
                case APPROVE_RESERVATION -> reservationController.approveReservation(request);

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
        } catch (ResponseException e) {
            logger.error("Памылка падчас апрацоўкі запыту: " + e.getMessage() + '\n' + e.getStack());
            return new Response(false, e.getMessage(), null);
        }
    }
}
