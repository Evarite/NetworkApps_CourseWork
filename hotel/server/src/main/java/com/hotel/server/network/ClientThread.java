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
    private final AccountController accountController = new AccountController();
    private final RoomController roomController = new RoomController();
    private final GuestController guestController = new GuestController();
    private final EmployeeController employeeController = new EmployeeController();
    private final ReservationController reservationController = new ReservationController();

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        logger.info("Запушчаны паток для кліента: " + socket.getInetAddress());

        try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            boolean running = true;
            while (running) {
                Request request = (Request) in.readObject();
                logger.info("Аперацыя: " + request.getOperation() + " | Кліент: " + socket.getInetAddress());

                Response response = processRequest(request);

                out.writeObject(response);
                out.flush();

                if (request.getOperation() == Operation.DISCONNECT) {
                    logger.info("Кліент адлучыўся: " + socket.getInetAddress());
                    running = false;
                }
            }
        } catch (EOFException e) {
            logger.warn("Кліент закрыў злучэнне без DISCONNECT: " + socket.getInetAddress());
        } catch (Exception e) {
            logger.error("Памылка ў патоку кліента " + socket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Памылка пры закрыцці сокета: " + e.getMessage());
            }
        }
    }

    private Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case LOGIN -> accountController.login(request);
                case REGISTER -> accountController.register(request);
                case UPDATE_ACCOUNT -> accountController.updateAccount(request);

                case ADD_ROOM -> roomController.addRoom(request);
                case CLOSE_ROOM -> roomController.closeRoom(request);
                case DELETE_ROOM -> roomController.deleteRoom(request);
                case UPDATE_ROOM -> roomController.updateRoom(request);
                case GET_ALL_ROOMS -> roomController.getAllRooms(request);
                case OPEN_ROOM -> roomController.openRoom(request);
                case GET_AVAILABLE_ROOMS -> roomController.getAvailableRooms(request);

                case CREATE_RESERVATION -> reservationController.createReservation(request);
                case CANCEL_RESERVATION -> reservationController.cancelReservation(request);
                case CHECK_OUT -> reservationController.checkOut(request);
                case GET_MY_RESERVATIONS -> reservationController.getMyReservations(request);
                case GET_ALL_RESERVATIONS -> reservationController.getAllReservations(request);
                case APPROVE_RESERVATION -> reservationController.approveReservation(request);
                case GET_PENDING_RESERVATIONS -> reservationController.getPendingReservations(request);
                case GET_APPROVED_RESERVATIONS -> reservationController.getApprovedReservations(request);
                case GET_MY_RESERVATIONS_AFTER_NOW -> reservationController.getMyReservationsAfterNow(request);

                case GET_ALL_EMPLOYEES -> employeeController.getAllEmployees(request);
                case HIRE_EMPLOYEE -> employeeController.hireEmployee(request);
                case FIRE_EMPLOYEE -> employeeController.fireEmployee(request);
                case CHANGE_ROLE -> employeeController.changeRole(request);

                case GET_ALL_GUESTS -> guestController.getAllGuests(request);
                case GET_ALL_GUESTS_WITH_RESERVATIONS -> guestController.getAllGuestsWithReservations(request);

                case DISCONNECT -> new Response(true, "Злучэнне завершана", null);

                default -> new Response(false, "Невядомая аперацыя", null);
            };
        } catch (ResponseException e) {
            logger.error("ResponseException: " + e.getMessage());
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Неспадзяваная памылка: " + e.getMessage(), e);
            return new Response(false, "Памылка сервера: " + e.getMessage(), null);
        }
    }
}
