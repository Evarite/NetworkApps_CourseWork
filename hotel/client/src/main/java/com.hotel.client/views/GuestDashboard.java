package com.hotel.client.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hotel.client.ui.SceneManager;
import com.hotel.client.components.TableHelper;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.dto.ReservationRequest;
import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GuestDashboard extends ViewBase {

    private final LoginResponse session;

    private VBox roomsView;
    private VBox availRoomsView;
    private VBox myResView;
    private VBox bookView;
    private VBox profileView;

    private Button activeBtn;

    public GuestDashboard(LoginResponse session) {
        this.session = session;
    }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("hotel-scene");

        root.setLeft(buildSidebar(root));
        root.setTop(buildHeader("Гатэль — Кабінет госця",
                session.getAccount(), session.getPosition()));

        root.setCenter(scrollWrap(buildRoomsView()));

        return new Scene(root, 1280, 760);
    }

    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("hotel-sidebar");

        VBox logoBox = new VBox(2);
        logoBox.getStyleClass().add("hotel-sidebar-header");
        Label logo = new Label("🏨 HOTEL");
        logo.getStyleClass().add("hotel-logo-label");
        Label sub = new Label("Кабінет госця");
        sub.getStyleClass().add("hotel-logo-sub");
        logoBox.getChildren().addAll(logo, sub);

        Button btnAllRooms = sidebarBtn("🛏  Усе нумары");
        Button btnAvailRooms = sidebarBtn("✅  Свабодныя нумары");
        Button btnBook = sidebarBtn("📋  Забраніраваць");
        Button btnMyRes = sidebarBtn("🗓  Мае браніраванні");
        Button btnProfile = sidebarBtn("⚙️  Мой акаўнт");

        Button[] allBtns = {btnAllRooms, btnAvailRooms, btnBook, btnMyRes, btnProfile};

        btnAllRooms.setOnAction(e -> {
            activateBtn(btnAllRooms, allBtns);
            root.setCenter(scrollWrap(buildRoomsView()));
        });
        btnAvailRooms.setOnAction(e -> {
            activateBtn(btnAvailRooms, allBtns);
            root.setCenter(scrollWrap(buildAvailableRoomsView()));
        });
        btnBook.setOnAction(e -> {
            activateBtn(btnBook, allBtns);
            root.setCenter(scrollWrap(buildBookView()));
        });
        btnMyRes.setOnAction(e -> {
            activateBtn(btnMyRes, allBtns);
            root.setCenter(scrollWrap(buildMyReservationsView()));
        });
        btnProfile.setOnAction(e -> {
            activateBtn(btnProfile, allBtns);
            root.setCenter(scrollWrap(buildProfileView()));
        });

        activateBtn(btnAllRooms, allBtns);

        sidebar.getChildren().addAll(logoBox,
                sidebarSection("НУМАРЫ"),
                btnAllRooms, btnAvailRooms,
                sidebarSection("БРАНІРАВАННІ"),
                btnBook, btnMyRes,
                sidebarSection("НАЛАДЫ"),
                btnProfile);

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        return sidebar;
    }

    private VBox buildRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(500);

        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, false));

        HBox toolbar = new HBox(8, refresh);
        toolbar.getStyleClass().add("hotel-toolbar");

        VBox content = card("Усе нумары гатэля", toolbar, table, msg);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().add(content);

        loadRooms(table, msg, false);
        return box;
    }

    private VBox buildAvailableRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(500);

        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, true));

        HBox toolbar = new HBox(8, refresh);
        toolbar.getStyleClass().add("hotel-toolbar");

        VBox content = card("Свабодныя нумары", toolbar, table, msg);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().add(content);

        loadRooms(table, msg, true);
        return box;
    }

    private VBox buildMyReservationsView() {
        VBox box = contentBox();
        TableView<Reservation> table = TableHelper.reservationTable();
        table.setPrefHeight(400);

        Label msg = errorLabel();

        Button cancelBtn = new Button("✗  Скасаваць браніраванне");
        cancelBtn.getStyleClass().add("hotel-btn-danger");
        cancelBtn.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener(
                (o, old, cur) -> cancelBtn.setDisable(cur == null ||
                        cur.getStatus() == Reservation.Status.CANCELLED ||
                        cur.getStatus() == Reservation.Status.CHECKED_OUT));

        cancelBtn.setOnAction(e -> {
            Reservation sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (!confirm("Скасаванне", "Скасаваць браніраванне #" + sel.getId() + "?")) return;
            try {
                String json = mapper.writeValueAsString(sel.getId());
                Response resp = send(new Request(Operation.CANCEL_RESERVATION, json));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    loadMyReservations(table, msg);
                } else {
                    showError(msg, resp != null ? resp.getMessage() : "Памылка сервера");
                }
            } catch (Exception ex) {
                showError(msg, ex.getMessage());
            }
        });

        Button refresh = refreshBtn(() -> loadMyReservations(table, msg));
        HBox toolbar = new HBox(8, refresh, cancelBtn);
        toolbar.getStyleClass().add("hotel-toolbar");

        VBox content = card("Мае браніраванні", toolbar, table, msg);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().add(content);

        loadMyReservations(table, msg);
        return box;
    }

    private VBox buildBookView() {
        VBox box = contentBox();

        // Выпадаючы спіс нумароў
        ComboBox<Room> roomCombo = new ComboBox<>();
        roomCombo.getStyleClass().add("hotel-combo");
        roomCombo.setMaxWidth(Double.MAX_VALUE);
        roomCombo.setPromptText("Выберыце нумар");
        roomCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Room r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" :
                        "#" + r.getNumber() + " — " + TableHelper.roomTypeStr(r.getType())
                                + " | " + String.format("%.0f BYN/ноч", r.getPrice()));
            }
        });
        roomCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Room r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" :
                        "#" + r.getNumber() + " — " + TableHelper.roomTypeStr(r.getType()));
            }
        });

        // Загрузка свабодных нумароў
        try {
            Response resp = send(new Request(Operation.GET_AVAILABLE_ROOMS, null));
            if (resp != null && resp.isSuccess()) {
                List<Room> rooms = mapper.readValue(resp.getData(), new TypeReference<>() {
                });
                roomCombo.getItems().addAll(rooms);
            }
        } catch (Exception ex) {
        }

        TextField dateFld = field("Дата заезду (гггг-мм-дд)");
        Spinner<Integer> durSpinner = new Spinner<>(1, 365, 1);
        durSpinner.setEditable(true);
        durSpinner.setMaxWidth(Double.MAX_VALUE);

        Label errLbl = errorLabel();

        Button bookBtn = new Button("Забраніраваць нумар");
        bookBtn.getStyleClass().add("hotel-btn-primary");

        bookBtn.setOnAction(e -> {
            Room selRoom = roomCombo.getValue();
            if (selRoom == null) {
                showError(errLbl, "Выберыце нумар");
                return;
            }
            String dateStr = dateFld.getText().trim();
            if (dateStr.isBlank()) {
                showError(errLbl, "Увядзіце дату заезду");
                return;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException ex) {
                showError(errLbl, "Няправільны фармат даты. Выкарыстоўвайце гггг-мм-дд");
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showError(errLbl, "Дата заезду не можа быць у мінулым");
                return;
            }
            int dur = durSpinner.getValue();
            try {
                ReservationRequest req = new ReservationRequest(
                        session.getAccount().getId(), selRoom.getNumber(), date, dur);
                String json = mapper.writeValueAsString(req);
                Response resp = send(new Request(Operation.CREATE_RESERVATION, json));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(errLbl, resp.getMessage());
                    roomCombo.setValue(null);
                    dateFld.clear();
                    durSpinner.getValueFactory().setValue(1);

                    roomCombo.getItems().clear();
                    Response r2 = send(new Request(Operation.GET_AVAILABLE_ROOMS, null));
                    if (r2 != null && r2.isSuccess()) {
                        List<Room> rooms = mapper.readValue(r2.getData(), new TypeReference<>() {
                        });
                        roomCombo.getItems().addAll(rooms);
                    }
                } else {
                    showError(errLbl, resp != null ? resp.getMessage() : "Памылка сервера");
                }
            } catch (Exception ex) {
                showError(errLbl, ex.getMessage());
            }
        });

        VBox form = new VBox(10,
                fieldLabel("Свабодны нумар"), roomCombo,
                fieldLabel("Дата заезду"), dateFld,
                fieldLabel("Колькасць начовак"), durSpinner,
                errLbl,
                bookBtn
        );
        form.setMaxWidth(480);

        box.getChildren().add(card("Забраніраваць нумар", form));
        return box;
    }

    private VBox buildProfileView() {
        VBox box = contentBox();
        var acc = session.getAccount();

        TextField emailFld = field("Email");
        emailFld.setText(acc.getEmail());
        TextField firstFld = field("Імя");
        firstFld.setText(acc.getFirstName());
        TextField lastFld = field("Прозвішча");
        lastFld.setText(acc.getLastName());
        PasswordField passFld = passField("Новы пароль (абавязкова)");

        Label errLbl = errorLabel();

        Button saveBtn = new Button("Захаваць змены");
        saveBtn.getStyleClass().add("hotel-btn-primary");

        saveBtn.setOnAction(e -> {
            if (passFld.getText().isBlank()) {
                showError(errLbl, "Для захавання увядзіце пароль");
                return;
            }
            try {
                var reqMap = mapper.createObjectNode();
                reqMap.put("accountId", acc.getId());
                reqMap.put("newEmail", emailFld.getText().trim());
                reqMap.put("newFirstName", firstFld.getText().trim());
                reqMap.put("newLastName", lastFld.getText().trim());
                reqMap.put("newPassword", passFld.getText());
                Response resp = send(new Request(Operation.UPDATE_ACCOUNT, mapper.writeValueAsString(reqMap)));
                if (resp != null && resp.isSuccess()) showSuccess(errLbl, resp.getMessage());
                else showError(errLbl, resp != null ? resp.getMessage() : "Памылка сервера");
            } catch (Exception ex) {
                showError(errLbl, ex.getMessage());
            }
        });

        VBox form = new VBox(10,
                fieldLabel("Email"), emailFld,
                fieldLabel("Імя"), firstFld,
                fieldLabel("Прозвішча"), lastFld,
                fieldLabel("Новы пароль"), passFld,
                errLbl, saveBtn);
        form.setMaxWidth(420);

        box.getChildren().add(card("Рэдагаваць акаўнт", form));
        return box;
    }

    private void loadRooms(TableView<Room> table, Label msg, boolean onlyAvailable) {
        try {
            Operation op = onlyAvailable ? Operation.GET_AVAILABLE_ROOMS : Operation.GET_ALL_ROOMS;
            Response resp = send(new Request(op, null));
            if (resp != null && resp.isSuccess()) {
                List<Room> rooms = mapper.readValue(resp.getData(), new TypeReference<>() {
                });
                table.setItems(FXCollections.observableArrayList(rooms));
                clearMsg(msg);
            } else {
                showError(msg, resp != null ? resp.getMessage() : "Памылка сервера");
            }
        } catch (Exception ex) {
            showError(msg, ex.getMessage());
        }
    }

    private void loadMyReservations(TableView<Reservation> table, Label msg) {
        try {
            String json = mapper.writeValueAsString(session.getAccount().getId());
            Response resp = send(new Request(Operation.GET_MY_RESERVATIONS, json));
            if (resp != null && resp.isSuccess()) {
                List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {
                });
                table.setItems(FXCollections.observableArrayList(list));
                clearMsg(msg);
            } else {
                showError(msg, resp != null ? resp.getMessage() : "Памылка сервера");
            }
        } catch (Exception ex) {
            showError(msg, ex.getMessage());
        }
    }
}
