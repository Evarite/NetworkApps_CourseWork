package com.hotel.client.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.client.network.ServerClient;
import com.hotel.common.dto.ChangeRoleRequest;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.entities.Employee;
import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ObjectMapper mapper = new ObjectMapper();
    private final Scanner scanner = new Scanner(System.in);
    private final ServerClient server;

    private Account account;
    private Employee.Position position;

    public Menu(ServerClient server) {
        mapper.registerModule(new JavaTimeModule());
        this.server = server;
    }

    public void start() {
        while (true) {
            System.out.println("Сістэма кіравання гатэлем");
            System.out.println("1. Уваход");
            System.out.println("2. Рэгістрацыя");
            System.out.println("3. Выхад");
            System.out.print("Ваш выбар: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> {
                    if (login()) routeToMenu();
                }
                case 2 -> {
                    if (register()) routeToMenu();
                }
                case 3 -> { return; }
                default -> System.out.println("Няправільны выбар. Паўтарыце.");
            }
        }
    }

    private void routeToMenu() {
        if (position == null) {
            guestMenu();
        } else {
            switch (position) {
                case RECEPTIONIST -> receptionistMenu();
                case MANAGER -> managerMenu();
                case ADMINISTRATOR -> administratorMenu();
            }
        }
        account = null;
        position = null;
    }

    private boolean login() {
        System.out.print("\nEmail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        try {
            String json = mapper.writeValueAsString(new LoginRequest(email, password));
            Response response = server.sendRequest(new Request(Operation.LOGIN, json));

            if (response == null) { System.out.println("Немагчыма злучыцца з серверам."); return false; }

            if (!response.isSuccess()) {
                System.out.println("Памылка ўваходу: " + response.getMessage());
                return false;
            }

            LoginResponse lr = mapper.readValue(response.getData(), LoginResponse.class);
            account = lr.getAccount();
            position = lr.getPosition();

            String role = position == null ? "Госць" : positionToStr(position);
            System.out.println("Вітаем, " + account.getFirstName() + " " + account.getLastName()
                    + "! Роля: " + role);
            return true;
        } catch (Exception e) {
            System.out.println("Памылка падчас уваходу: " + e.getMessage());
            return false;
        }
    }

    private boolean register() {
        System.out.println("\n--- Рэгістрацыя ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Пароль (мін. 6 сімвалаў): ");
        String password = scanner.nextLine();
        System.out.print("Імя: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Прозвішча: ");
        String lastName = scanner.nextLine().trim();

        LocalDate birthDate = readDate("Дата нараджэння (дд.мм.гггг): ");
        if (birthDate == null) return false;

        try {
            RegisterRequest req = new RegisterRequest(email, password, firstName, lastName, birthDate);
            String json = mapper.writeValueAsString(req);
            Response response = server.sendRequest(new Request(Operation.REGISTER, json));

            if (response == null) { System.out.println("Немагчыма злучыцца з серверам."); return false; }

            if (!response.isSuccess()) {
                System.out.println("Памылка рэгістрацыі: " + response.getMessage());
                return false;
            }

            LoginResponse lr = mapper.readValue(response.getData(), LoginResponse.class);
            account = lr.getAccount();
            position = null;
            System.out.println("Рэгістрацыя паспяховая! Вітаем, " + account.getFirstName() + "!");
            return true;
        } catch (Exception e) {
            System.out.println("Памылка падчас рэгістрацыі: " + e.getMessage());
            return false;
        }
    }

    private void guestMenu() {
        while (true) {
            System.out.println("\n========== МЕНЮ ГОСЦЯ ==========");
            System.out.println("1. " + Operation.GET_ALL_ROOMS);
            System.out.println("2. " + Operation.GET_AVAILABLE_ROOMS);
            System.out.println("3. " + Operation.CREATE_RESERVATION);
            System.out.println("4. " + Operation.CANCEL_RESERVATION);
            System.out.println("5. " + Operation.GET_MY_RESERVATIONS);
            System.out.println("6. " + Operation.UPDATE_ACCOUNT);
            System.out.println("7. " + Operation.DISCONNECT);
            System.out.print("Ваш выбар: ");

            switch (readInt()) {
                case 1 -> getAllRooms();
                case 2 -> getAvailableRooms();
                case 3 -> createReservation();
                case 4 -> cancelReservationByGuest();
                case 5 -> getMyReservations();
                case 6 -> updateAccount();
                case 7 -> { return; }
                default -> System.out.println("Няправільны выбар.");
            }
        }
    }

    private void receptionistMenu() {
        while (true) {
            System.out.println("\n========== МЕНЮ ПАРЦЬЕ ==========");
            System.out.println("1. " + Operation.GET_PENDING_RESERVATIONS);
            System.out.println("2. " + Operation.APPROVE_RESERVATION);
            System.out.println("3. " + Operation.CANCEL_RESERVATION);
            System.out.println("4. " + Operation.GET_ALL_ROOMS);
            System.out.println("5. " + Operation.GET_AVAILABLE_ROOMS);
            System.out.println("6. " + Operation.GET_ALL_GUESTS);
            System.out.println("7. " + Operation.UPDATE_ACCOUNT);
            System.out.println("8. " + Operation.DISCONNECT);
            System.out.print("Ваш выбар: ");

            switch (readInt()) {
                case 1 -> getPendingReservations();
                case 2 -> approveReservation();
                case 3 -> cancelReservationByStaff();
                case 4 -> getAllRooms();
                case 5 -> getAvailableRooms();
                case 6 -> getAllGuests();
                case 7 -> updateAccount();
                case 8 -> { return; }
                default -> System.out.println("Няправільны выбар.");
            }
        }
    }

    private void managerMenu() {
        while (true) {
            System.out.println("\n========== МЕНЮ МЭНЭДЖАРА ==========");
            System.out.println("1. " + Operation.GET_ALL_ROOMS);
            System.out.println("2. " + Operation.GET_AVAILABLE_ROOMS);
            System.out.println("3. " + Operation.CLOSE_ROOM);
            System.out.println("4. " + Operation.OPEN_ROOM);
            System.out.println("5. " + Operation.GET_APPROVED_RESERVATIONS);
            System.out.println("6. " + Operation.CHECK_OUT);
            System.out.println("7. " + Operation.GET_ALL_GUESTS_WITH_RESERVATIONS);
            System.out.println("8. " + Operation.UPDATE_ACCOUNT);
            System.out.println("9. " + Operation.DISCONNECT);
            System.out.print("Ваш выбар: ");

            switch (readInt()) {
                case 1 -> getAllRooms();
                case 2 -> getAvailableRooms();
                case 3 -> closeRoom();
                case 4 -> openRoom();
                case 5 -> getApprovedReservations();
                case 6 -> checkOut();
                case 7 -> getAllGuestsWithReservations();
                case 8 -> updateAccount();
                case 9 -> { return; }
                default -> System.out.println("Няправільны выбар.");
            }
        }
    }

    private void administratorMenu() {
        while (true) {
            System.out.println("\n========== МЕНЮ АДМІНІСТРАТАРА ==========");
            System.out.println("--- Нумары ---");
            System.out.println("1.  " + Operation.GET_ALL_ROOMS);
            System.out.println("2.  " + Operation.GET_AVAILABLE_ROOMS);
            System.out.println("3.  " + Operation.ADD_ROOM);
            System.out.println("4.  " + Operation.DELETE_ROOM);
            System.out.println("5.  " + Operation.CLOSE_ROOM);
            System.out.println("6.  " + Operation.OPEN_ROOM);
            System.out.println("--- Браніраванні ---");
            System.out.println("7.  " + Operation.GET_ALL_RESERVATIONS);
            System.out.println("8.  " + Operation.GET_PENDING_RESERVATIONS);
            System.out.println("9.  " + Operation.APPROVE_RESERVATION);
            System.out.println("10. " + Operation.CANCEL_RESERVATION);
            System.out.println("11. " + Operation.GET_APPROVED_RESERVATIONS);
            System.out.println("12. " + Operation.CHECK_OUT);
            System.out.println("--- Госці ---");
            System.out.println("13. " + Operation.GET_ALL_GUESTS);
            System.out.println("14. " + Operation.GET_ALL_GUESTS_WITH_RESERVATIONS);
            System.out.println("--- Супрацоўнікі ---");
            System.out.println("15. " + Operation.GET_ALL_EMPLOYEES);
            System.out.println("16. " + Operation.HIRE_EMPLOYEE);
            System.out.println("17. " + Operation.FIRE_EMPLOYEE);
            System.out.println("18. " + Operation.CHANGE_ROLE);
            System.out.println("--- Акаўнт ---");
            System.out.println("19. " + Operation.UPDATE_ACCOUNT);
            System.out.println("20. " + Operation.DISCONNECT);
            System.out.print("Ваш выбар: ");

            switch (readInt()) {
                case 1  -> getAllRooms();
                case 2  -> getAvailableRooms();
                case 3  -> addRoom();
                case 4  -> deleteRoom();
                case 5  -> closeRoom();
                case 6  -> openRoom();
                case 7  -> getAllReservations();
                case 8  -> getPendingReservations();
                case 9  -> approveReservation();
                case 10 -> cancelReservationByStaff();
                case 11 -> getApprovedReservations();
                case 12 -> checkOut();
                case 13 -> getAllGuests();
                case 14 -> getAllGuestsWithReservations();
                case 15 -> getAllEmployees();
                case 16 -> hireEmployee();
                case 17 -> fireEmployee();
                case 18 -> changeRole();
                case 19 -> updateAccount();
                case 20 -> { return; }
                default -> System.out.println("Няправільны выбар.");
            }
        }
    }

    private void getAllRooms() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_ALL_ROOMS, null));
            if (!checkResponse(resp)) return;
            List<Room> rooms = mapper.readValue(resp.getData(), new TypeReference<>() {});
            printRooms(rooms);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getAvailableRooms() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_AVAILABLE_ROOMS, null));
            if (!checkResponse(resp)) return;
            List<Room> rooms = mapper.readValue(resp.getData(), new TypeReference<>() {});
            printRooms(rooms);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void addRoom() {
        System.out.println("\n--- Дадаць нумар ---");
        try {
            System.out.print("Нумар пакоя: ");
            int number = readInt();
            System.out.print("Паверх: ");
            int floor = readInt();

            System.out.println("Тып нумара:");
            Room.Type[] types = Room.Type.values();
            for (int i = 0; i < types.length; i++)
                System.out.println((i + 1) + ". " + roomTypeToStr(types[i]));
            System.out.print("Выбар: ");
            int ti = readInt() - 1;
            if (ti < 0 || ti >= types.length) { System.out.println("Няправільны выбар."); return; }
            Room.Type type = types[ti];

            System.out.println("Месткасць:");
            Room.Capacity[] caps = Room.Capacity.values();
            for (int i = 0; i < caps.length; i++)
                System.out.println((i + 1) + ". " + capacityToStr(caps[i]));
            System.out.print("Выбар: ");
            int ci = readInt() - 1;
            if (ci < 0 || ci >= caps.length) { System.out.println("Няправільны выбар."); return; }
            Room.Capacity capacity = caps[ci];

            System.out.print("Апісанне: ");
            String description = scanner.nextLine().trim();
            System.out.print("Цана за ноч (BYN): ");
            float price = readFloat();

            Room room = new Room(number, floor, type, capacity, description, Room.Status.AVAILABLE, price);
            String json = mapper.writeValueAsString(room);
            Response resp = server.sendRequest(new Request(Operation.ADD_ROOM, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void deleteRoom() {
        System.out.println("\n--- Выдаліць нумар ---");
        getAllRooms();
        System.out.print("Увядзіце нумар пакоя для выдалення: ");
        int number = readInt();
        try {
            String json = mapper.writeValueAsString(number);
            Response resp = server.sendRequest(new Request(Operation.DELETE_ROOM, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void closeRoom() {
        System.out.println("\n--- Часова зачыніць нумар ---");
        getAllRooms();
        System.out.print("Нумар пакоя: ");
        int number = readInt();
        try {
            String json = mapper.writeValueAsString(number);
            Response resp = server.sendRequest(new Request(Operation.CLOSE_ROOM, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void openRoom() {
        System.out.println("\n--- Адчыніць нумар ---");
        getAllRooms();
        System.out.print("Нумар пакоя: ");
        int number = readInt();
        try {
            String json = mapper.writeValueAsString(number);
            Response resp = server.sendRequest(new Request(Operation.OPEN_ROOM, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void createReservation() {
        System.out.println("\n--- Забраніраваць нумар ---");
        getAvailableRooms();

        System.out.print("Нумар пакоя: ");
        int number = readInt();
        LocalDate date = readDate("Дата заезду (дд.мм.гггг): ");
        if (date == null) return;
        System.out.print("Колькасць начовак: ");
        int duration = readInt();

        try {
            ReservationRequest req = new ReservationRequest(account.getId(), number, date, duration);
            String json = mapper.writeValueAsString(req);
            Response resp = server.sendRequest(new Request(Operation.CREATE_RESERVATION, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void cancelReservationByGuest() {
        System.out.println("\n--- Скасаваць браніраванне ---");
        List<Reservation> list = fetchMyReservations();
        if (list == null || list.isEmpty()) return;

        System.out.print("Увядзіце ID браніравання для скасавання: ");
        int id = readInt();
        sendCancelReservation(id);
    }

    private void cancelReservationByStaff() {
        System.out.println("\n--- Скасаваць браніраванне ---");
        getPendingReservations();
        System.out.print("Увядзіце ID браніравання для скасавання: ");
        int id = readInt();
        sendCancelReservation(id);
    }

    private void sendCancelReservation(int id) {
        try {
            String json = mapper.writeValueAsString(id);
            Response resp = server.sendRequest(new Request(Operation.CANCEL_RESERVATION, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getPendingReservations() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_PENDING_RESERVATIONS, null));
            if (!checkResponse(resp)) return;
            List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {});
            if (list.isEmpty()) { System.out.println("Няма браніраванняў, якія чакаюць зацверджання."); return; }
            printReservations(list);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void approveReservation() {
        System.out.println("\n--- Зацвердзіць браніраванне ---");
        getPendingReservations();
        System.out.print("Увядзіце ID браніравання для зацверджання: ");
        int id = readInt();
        try {
            String json = mapper.writeValueAsString(id);
            Response resp = server.sendRequest(new Request(Operation.APPROVE_RESERVATION, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getApprovedReservations() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_APPROVED_RESERVATIONS, null));
            if (!checkResponse(resp)) return;
            List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {});
            if (list.isEmpty()) { System.out.println("Няма зацверджаных браніраванняў."); return; }
            printReservations(list);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void checkOut() {
        System.out.println("\n--- Выселіць госця ---");
        getApprovedReservations();
        System.out.print("Увядзіце ID браніравання: ");
        int id = readInt();
        try {
            String json = mapper.writeValueAsString(id);
            Response resp = server.sendRequest(new Request(Operation.CHECK_OUT, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getAllReservations() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_ALL_RESERVATIONS, null));
            if (!checkResponse(resp)) return;
            List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {});
            if (list.isEmpty()) { System.out.println("Браніраванняў не знойдзена."); return; }
            printReservations(list);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getMyReservations() {
        List<Reservation> list = fetchMyReservations();
        if (list != null && list.isEmpty())
            System.out.println("У вас няма браніраванняў.");
    }

    private List<Reservation> fetchMyReservations() {
        try {
            String json = mapper.writeValueAsString(account.getId());
            Response resp = server.sendRequest(new Request(Operation.GET_MY_RESERVATIONS, json));
            if (!checkResponse(resp)) return null;
            List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {});
            printReservations(list);
            return list;
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
            return null;
        }
    }

    // ==================== АПЕРАЦЫІ З ГАСЦЯМІ ====================

    private void getAllGuests() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_ALL_GUESTS, null));
            if (!checkResponse(resp)) return;
            // Выводзім як сырыя дадзеныя — у Guest няма назвы
            System.out.println("Госці (ID акаўнта | Рэйтынг | Кол-сць браніраванняў):");
            System.out.println(resp.getData());
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void getAllGuestsWithReservations() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_ALL_GUESTS_WITH_RESERVATIONS, null));
            if (!checkResponse(resp)) return;
            System.out.println("Госці з браніраваннямі:");
            System.out.println(resp.getData());
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    // ==================== АПЕРАЦЫІ З СУПРАЦОЎНІКАМІ ====================

    private void getAllEmployees() {
        try {
            Response resp = server.sendRequest(new Request(Operation.GET_ALL_EMPLOYEES, null));
            if (!checkResponse(resp)) return;
            System.out.println("Супрацоўнікі:");
            System.out.println(resp.getData());
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void hireEmployee() {
        System.out.println("\n--- Уладкаваць супрацоўніка ---");
        System.out.print("ID акаўнта: ");
        int accountId = readInt();

        System.out.println("Пасада:");
        System.out.println("1. Парцье");
        System.out.println("2. Мэнэджар");
        System.out.println("3. Адміністратар");
        System.out.print("Выбар: ");
        int pi = readInt();
        Employee.Position pos = switch (pi) {
            case 1 -> Employee.Position.RECEPTIONIST;
            case 2 -> Employee.Position.MANAGER;
            case 3 -> Employee.Position.ADMINISTRATOR;
            default -> null;
        };
        if (pos == null) { System.out.println("Няправільны выбар."); return; }

        System.out.print("Заробак (BYN): ");
        float salary = readFloat();
        LocalDate hireDate = readDate("Дата прыёму (дд.мм.гггг): ");
        if (hireDate == null) return;

        try {
            Employee employee = new Employee(accountId, pos, salary, hireDate);
            String json = mapper.writeValueAsString(employee);
            Response resp = server.sendRequest(new Request(Operation.HIRE_EMPLOYEE, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void fireEmployee() {
        System.out.println("\n--- Звольніць супрацоўніка ---");
        getAllEmployees();
        System.out.print("ID акаўнта супрацоўніка: ");
        int accountId = readInt();
        try {
            String json = mapper.writeValueAsString(accountId);
            Response resp = server.sendRequest(new Request(Operation.FIRE_EMPLOYEE, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    private void changeRole() {
        System.out.println("\n--- Змяніць пасаду ---");
        getAllEmployees();
        System.out.print("ID акаўнта супрацоўніка: ");
        int accountId = readInt();

        System.out.println("Новая пасада:");
        System.out.println("1. Парцье");
        System.out.println("2. Мэнэджар");
        System.out.println("3. Адміністратар");
        System.out.print("Выбар: ");
        int pi = readInt();
        Employee.Position pos = switch (pi) {
            case 1 -> Employee.Position.RECEPTIONIST;
            case 2 -> Employee.Position.MANAGER;
            case 3 -> Employee.Position.ADMINISTRATOR;
            default -> null;
        };
        if (pos == null) { System.out.println("Няправільны выбар."); return; }

        try {
            ChangeRoleRequest req = new ChangeRoleRequest(accountId, pos);
            String json = mapper.writeValueAsString(req);
            Response resp = server.sendRequest(new Request(Operation.CHANGE_ROLE, json));
            checkResponse(resp);
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    // ==================== АКАЎНТ ====================

    private void updateAccount() {
        System.out.println("\n--- Рэдагаваць акаўнт ---");
        System.out.println("Бягучы email: " + account.getEmail());
        System.out.print("Новы email (Enter — пакінуць): ");
        String email = scanner.nextLine().trim();
        if (email.isBlank()) email = account.getEmail();

        System.out.println("Бягучае імя: " + account.getFirstName());
        System.out.print("Новае імя (Enter — пакінуць): ");
        String fn = scanner.nextLine().trim();
        if (fn.isBlank()) fn = account.getFirstName();

        System.out.println("Бягучае прозвішча: " + account.getLastName());
        System.out.print("Новае прозвішча (Enter — пакінуць): ");
        String ln = scanner.nextLine().trim();
        if (ln.isBlank()) ln = account.getLastName();

        System.out.print("Новы пароль (Enter — не змяняць): ");
        String pw = scanner.nextLine();
        if (pw.isBlank()) {
            System.out.println("Для змены пароля ўвядзіце яго. Аперацыя скасавана.");
            return;
        }

        try {
            // Выкарыстоўваем унутраны DTO AccountController.UpdateAccountRequest
            var reqMap = mapper.createObjectNode();
            reqMap.put("accountId", account.getId());
            reqMap.put("newEmail", email);
            reqMap.put("newFirstName", fn);
            reqMap.put("newLastName", ln);
            reqMap.put("newPassword", pw);
            String json = mapper.writeValueAsString(reqMap);
            Response resp = server.sendRequest(new Request(Operation.UPDATE_ACCOUNT, json));
            if (checkResponse(resp)) {
                account = new Account(account.getId(), email, fn, ln, null, account.getBirthDate());
            }
        } catch (Exception e) {
            System.out.println("Памылка: " + e.getMessage());
        }
    }

    // ==================== ДАПАМОЖНЫЯ МЕТАДЫ ====================

    /**
     * Выпраўлена: InputMismatchException больш не падае праграму.
     * Вяртае -1 пры няправільным уводзе.
     */
    private int readInt() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Увядзіце лічбу.");
            return -1;
        }
    }

    private float readFloat() {
        try {
            String line = scanner.nextLine().trim();
            return Float.parseFloat(line.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Няправільны фармат ліку. Прынята значэнне 0.");
            return 0f;
        }
    }

    private LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return LocalDate.parse(input, DATE_FMT);
        } catch (DateTimeParseException e) {
            System.out.println("Няправільны фармат даты. Выкарыстоўвайце дд.мм.гггг.");
            return null;
        }
    }

    /**
     * Правяраем адказ сервера і выводзім паведамленне.
     * Вяртае true калі паспяхова.
     */
    private boolean checkResponse(Response resp) {
        if (resp == null) {
            System.out.println("Няма адказу ад сервера.");
            return false;
        }
        System.out.println(resp.isSuccess() ? "✓ " + resp.getMessage() : "✗ " + resp.getMessage());
        return resp.isSuccess();
    }

    private void printRooms(List<Room> rooms) {
        if (rooms.isEmpty()) { System.out.println("Нумароў не знойдзена."); return; }
        System.out.printf("%-6s %-6s %-15s %-10s %-10s %-12s%n",
                "Нумар", "Паверх", "Тып", "Мяшч.", "Статус", "Цана/ноч");
        System.out.println("-".repeat(65));
        for (Room r : rooms) {
            System.out.printf("%-6d %-6d %-15s %-10s %-10s %-10.2f BYN%n",
                    r.getNumber(), r.getFloor(),
                    roomTypeToStr(r.getType()),
                    capacityToStr(r.getCapacity()),
                    roomStatusToStr(r.getStatus()),
                    r.getPrice());
            if (r.getDescription() != null && !r.getDescription().isBlank())
                System.out.println("  Апісанне: " + r.getDescription());
        }
    }

    private void printReservations(List<Reservation> list) {
        if (list.isEmpty()) { System.out.println("Браніраванняў не знойдзена."); return; }
        System.out.printf("%-5s %-8s %-7s %-12s %-8s %-12s%n",
                "ID", "Госць ID", "Нумар", "Дата заезду", "Начоў", "Статус");
        System.out.println("-".repeat(60));
        for (Reservation r : list) {
            System.out.printf("%-5d %-8d %-7d %-12s %-8d %-12s%n",
                    r.getId(), r.getGuestId(), r.getRoomNumber(),
                    r.getReservationDate().format(DATE_FMT),
                    r.getDuration(),
                    reservationStatusToStr(r.getStatus()));
        }
    }

    private String positionToStr(Employee.Position pos) {
        return switch (pos) {
            case RECEPTIONIST -> "Парцье";
            case MANAGER -> "Мэнэджар";
            case ADMINISTRATOR -> "Адміністратар";
        };
    }

    private String roomTypeToStr(Room.Type t) {
        return switch (t) {
            case STANDARD -> "Стандарт";
            case SUPERIOR -> "Супэрыёр";
            case JUNIOR_SUITE -> "Джуніёр сюіт";
            case SUITE -> "Сюіт";
            case APARTMENTS -> "Апартаменты";
            case PRESIDENT -> "Прэзідэнт";
        };
    }

    private String capacityToStr(Room.Capacity c) {
        return switch (c) {
            case SINGLE -> "1 ч.";
            case DOUBLE -> "2 ч.";
            case TWIN -> "2 ч. (2 ложкі)";
            case TRIPLE -> "3 ч.";
            case FAMILY -> "Сямейны";
        };
    }

    private String roomStatusToStr(Room.Status s) {
        return switch (s) {
            case AVAILABLE -> "Свабодны";
            case OCCUPIED -> "Занятны";
            case MAINTENANCE -> "Тэхабсл.";
        };
    }

    private String reservationStatusToStr(Reservation.Status s) {
        return switch (s) {
            case PENDING -> "Чакае";
            case APPROVED -> "Зацверджана";
            case CANCELLED -> "Скасавана";
            case CHECKED_OUT -> "Выселены";
        };
    }
}
