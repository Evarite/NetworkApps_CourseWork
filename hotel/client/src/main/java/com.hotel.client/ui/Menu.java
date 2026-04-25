package com.hotel.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.client.network.ServerClient;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import org.mindrot.jbcrypt.BCrypt;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Scanner scanner = new Scanner(System.in);
    private final ServerClient server;
    private Account account;

    public Menu(ServerClient server) {
        mapper.registerModule(new JavaTimeModule());
        this.server = server;
    }

    public void start() {
        while (true) {
            System.out.println("""
                    1. Уваход
                    2. Рэгістрацыя
                    3. Выхад
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            Response response = new Response(false, null, null);
            switch (choice) {
                case 1:
                    response = login();
                    break;
                case 2:
                    response = register();
                    break;
                case 3:
                    return;
            }

            if (!response.isSuccess())
                account = null;
            else try {
                account = mapper.readValue(response.getData(), Account.class);
            } catch (Exception e) {
                throw new RuntimeException("Памылка падчас атрымання дадзеных карыстальніка" + e.getMessage());
            }

            if (account != null) {
                guestMenu();
                break;
            }
        }
    }

    private void guestMenu() {
        ArrayList<Operation> operations = new ArrayList<>(List.of(Operation.GET_ALL_ROOMS,
                Operation.GET_AVAILABLE_ROOMS, Operation.CREATE_RESERVATION, Operation.CANCEL_RESERVATION,
                Operation.CHECK_OUT, Operation.UPDATE_ACCOUNT, Operation.DISCONNECT));

        int choice;
        while (true) {
            System.out.println("Меню:");
            int i = 1;
            for(var operation : operations) {
                System.out.println(i++ + ". " + operation.toString() + '\n');
            }

            System.out.print("Ваш выбар: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> getAllRooms();
                case 2 -> getAvailableRooms();
                case 3 -> createReservation();
                case 4 -> cancelReservation();
                case 5 -> checkOut();
                case 6 -> updateAccount();
                case 7 -> {
                    disconnect();
                    return;
                }
                default -> System.out.println("Няправільны выбар. Паўтарыце спробу");
            }
        }
    }

    private void receptionistMenu() {
        ArrayList<Operation> operations = new ArrayList<>(List.of(Operation.APPROVE_RESERVATION,
                Operation.GET_ALL_ROOMS, Operation.GET_AVAILABLE_ROOMS, Operation.GET_ALL_GUESTS,
                Operation.UPDATE_ACCOUNT, Operation.DISCONNECT));

        int choice;
        while (true) {
            System.out.println("Меню:");
            int i = 1;
            for(var operation : operations) {
                System.out.println(i++ + ". " + operation.toString() + '\n');
            }

            System.out.print("Ваш выбар: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> approveReservation();
                case 2 -> getAllRooms();
                case 3 -> getAvailableRooms();
                case 4 -> cancelReservation();
                case 5 -> updateAccount();
                case 6 -> {
                    disconnect();
                    return;
                }
                default -> System.out.println("Няправільны выбар. Паўтарыце спробу");
            }
        }
    }

    private Response login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            LoginRequest loginRequest = new LoginRequest(email, hashedPassword);
            String json = mapper.writeValueAsString(loginRequest);
            return server.sendRequest(new Request(Operation.LOGIN, json));
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }
    }

    private Response register() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        System.out.print("Імя: ");
        String firstName = scanner.nextLine();
        System.out.print("Прозьвішча: ");
        String lastName = scanner.nextLine();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.print("Дата нараджэньня (дд.ММ.ГГГГ): ");
        String input = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(input, formatter);

        try {
            RegisterRequest registerRequest = new RegisterRequest(email, hashedPassword, firstName, lastName, date);
            String json = mapper.writeValueAsString(registerRequest);
            return server.sendRequest(new Request(Operation.REGISTER, json));
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }
    }

    private void disconnect() {
        server.sendRequest(new Request(Operation.DISCONNECT, null));
        server.disconnect();
    }

    private void getAllRooms() {
        Response response = server.sendRequest(new Request(Operation.GET_ALL_ROOMS, null));
        try {
            List<Room> rooms = mapper.readValue(response.getData(), List.class);
            System.out.println("Атрымана пакояў: " + rooms.size());
            for(var room : rooms) {
                System.out.println(room.getNumber() + '\n' + room.getDescription());
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }
    }

    private void getAvailableRooms() {
        Response response = server.sendRequest(new Request(Operation.GET_AVAILABLE_ROOMS, null));
        try {
            List<Room> rooms = mapper.readValue(response.getData(), List.class);
            System.out.println("Атрымана пакояў: " + rooms.size());
            for(var room : rooms) {
                System.out.println(room.getNumber() + '\n' + room.getDescription());
            }
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }
    }

    private void createReservation() {
        getAvailableRooms();

        System.out.print("Нумар пакоя: ");
        int number = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Дата заезду: ");
        String input = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(input, formatter);

        System.out.print("Працягласць браніравання: ");
        int duration = scanner.nextInt();
        scanner.nextLine();

        ReservationRequest request = new ReservationRequest(account.getId(), number, date, duration);

        Response response;

        try {
            String json = mapper.writeValueAsString(request);
            response = server.sendRequest(new Request(Operation.CREATE_RESERVATION, json));
        } catch(Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }

        if(response.isSuccess())
            System.out.println("Запыт паспяхова адпраўлены. Чакаецца пацверджанне.");
        else
            System.out.println("Не атрымалася выканаць аперацыю.");
    }

    private void cancelReservation() {
        //I want a list of reservations to be displayed, where the user will click on a reservation
        //Then the number of the reservation is taken and transferred. This applies to many choose operations
        //In the program.

        var reservations = getMyReservationsAfterNow();
        System.out.println("Выбярыце нумар пакоя, каб скасаваць браніраванне");
        int number = scanner.nextInt();
        scanner.nextLine();

        String json;
        try {
            json = mapper.writeValueAsString(number);
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }

        Response response = server.sendRequest(new Request(Operation.CANCEL_RESERVATION, json));

        if(response.isSuccess())
            System.out.println("Браніраванне паспяхова скасаванае.");
        else
            System.out.println("Не атрымалася выканаць аперацыю.");
    }

    private void checkOut() {

    }

    private void updateAccount() {
        //Easier to do with GUI
    }

    private void approveReservation() {

    }

    private void getAllGuests() {
        Response response = server.sendRequest(new Request(Operation.GET_ALL_GUESTS, null));
    }

    //Functions below are not called from menu
    private List<Reservation> getMyReservationsAfterNow() {
        try {
            String json = mapper.writeValueAsString(account.getId());
            Response response = server.sendRequest(new Request(Operation.GET_MY_RESERVATIONS_AFTER_NOW, json));
            List<Reservation> reservations = mapper.readValue(response.getData(), List.class);
            return reservations;
        } catch (Exception e) {
            throw new RuntimeException("Памылка падчас выканання запыту: " + e.getMessage());
        }
    }
}
