package com.hotel.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.client.network.ServerClient;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Scanner scanner = new Scanner(System.in);
    private final ServerClient server;

    public Menu(ServerClient server) {
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
            scanner.next();

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

            Account account;
            try {
                account = mapper.readValue(response.getData(), Account.class);
            } catch (Exception e) {
                throw new RuntimeException("Памылка падчас атрымання дадзеных карыстальніка" + e.getMessage());
            }
            if (account != null) {
                guestMenu(account);
                break;
            }
        }
    }

    private void guestMenu(Account account) {
        ArrayList<Operation> operations = new ArrayList<Operation>(List.of(Operation.GET_ALL_ROOMS,
                Operation.GET_AVAILABLE_ROOMS, Operation.CREATE_RESERVATION, Operation.CANCEL_RESERVATION,
                Operation.CHECK_OUT, Operation.UPDATE_ACCOUNT, Operation.DISCONNECT));
        while (true) {
            System.out.println("Меню:");
            int i = 1;
            for(var operation : operations) {
                System.out.println(i++ + ". " + operation.toString() + '\n');
            }
        }
    }

    private Response login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("Пароль: ");
        String password = scanner.nextLine();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            LoginRequest loginRequest = new LoginRequest(email, hashedPassword);
            String json = mapper.writeValueAsString(loginRequest);
            return server.sendRequest(new Request(Operation.LOGIN, json));
        } catch (Exception e) {
            throw new RuntimeException("Не атрымалася адправіць запыт: " + e.getMessage());
        }
    }

    private Response register() {
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Пароль: ");
        String password = scanner.nextLine();
        System.out.println("Імя: ");
        String firstName = scanner.nextLine();
        System.out.println("Прозьвішча: ");
        String lastName = scanner.nextLine();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        System.out.println("Дата нараджэньня (дд.ММ.ГГГГ): ");
        String input = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(input, formatter);

        try {
            RegisterRequest registerRequest = new RegisterRequest(email, hashedPassword, firstName, lastName, date);
            String json = mapper.writeValueAsString(registerRequest);
            return server.sendRequest(new Request(Operation.REGISTER, json));
        } catch (Exception e) {
            throw new RuntimeException("Не атрымалася адправіць запыт: " + e.getMessage());
        }
    }
}
