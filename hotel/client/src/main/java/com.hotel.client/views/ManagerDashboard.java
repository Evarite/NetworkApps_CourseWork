package com.hotel.client.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hotel.client.components.RatingDialog;
import com.hotel.client.components.TableHelper;
import com.hotel.common.dto.CheckOutRequest;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.entities.Guest;
import com.hotel.common.entities.Reservation;
import com.hotel.common.entities.Room;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class ManagerDashboard extends ViewBase {

    private final LoginResponse session;

    public ManagerDashboard(LoginResponse session) { this.session = session; }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("hotel-scene");
        root.setTop(buildHeader("Кабінет мэнэджара", session.getAccount(), session.getPosition()));
        root.setLeft(buildSidebar(root));
        root.setCenter(scrollWrap(allRoomsView()));
        return new Scene(root, 1280, 760);
    }

    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("hotel-sidebar");

        VBox logo = new VBox(2);
        logo.getStyleClass().add("hotel-sidebar-header");
        Label l1 = new Label("🏨 HOTEL"); l1.getStyleClass().add("hotel-logo-label");
        Label l2 = new Label("Мэнэджар"); l2.getStyleClass().add("hotel-logo-sub");
        logo.getChildren().addAll(l1, l2);

        Button b1 = sidebarBtn("🛏  Усе нумары");
        Button b2 = sidebarBtn("✅  Свабодныя нумары");
        Button b3 = sidebarBtn("🔒  Зачыніць нумар");
        Button b4 = sidebarBtn("🔓  Адчыніць нумар");
        Button b5 = sidebarBtn("🗓  Зацверджаныя браніраванні");
        Button b6 = sidebarBtn("🚪  Выселіць госця");
        Button b7 = sidebarBtn("👥  Госці з браніраваннямі");
        Button b8 = sidebarBtn("⚙️  Акаўнт");
        Button[] all = {b1, b2, b3, b4, b5, b6, b7, b8};

        b1.setOnAction(e -> { activateBtn(b1, all); root.setCenter(scrollWrap(allRoomsView())); });
        b2.setOnAction(e -> { activateBtn(b2, all); root.setCenter(scrollWrap(availRoomsView())); });
        b3.setOnAction(e -> { activateBtn(b3, all); root.setCenter(scrollWrap(closeRoomView())); });
        b4.setOnAction(e -> { activateBtn(b4, all); root.setCenter(scrollWrap(openRoomView())); });
        b5.setOnAction(e -> { activateBtn(b5, all); root.setCenter(scrollWrap(approvedResView())); });
        b6.setOnAction(e -> { activateBtn(b6, all); root.setCenter(scrollWrap(checkOutView())); });
        b7.setOnAction(e -> { activateBtn(b7, all); root.setCenter(scrollWrap(guestsWithResView())); });
        b8.setOnAction(e -> { activateBtn(b8, all); root.setCenter(scrollWrap(profileView())); });

        activateBtn(b1, all);
        sidebar.getChildren().addAll(logo,
                sidebarSection("НУМАРЫ"),      b1, b2, b3, b4,
                sidebarSection("БРАНІРАВАННІ"), b5, b6,
                sidebarSection("ГОСЦІ"),        b7,
                sidebarSection("НАЛАДЫ"),       b8);
        return sidebar;
    }

    private VBox allRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(520);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, false));
        box.getChildren().add(card("Усе нумары гатэля", new HBox(8, refresh), table, msg));
        loadRooms(table, msg, false);
        return box;
    }

    private VBox availRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(520);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, true));
        box.getChildren().add(card("Свабодныя нумары", new HBox(8, refresh), table, msg));
        loadRooms(table, msg, true);
        return box;
    }

    private VBox closeRoomView() {
        VBox box = contentBox();
        Label msg = errorLabel();
        ComboBox<Room> combo = buildRoomCombo(msg, false);

        Button closeBtn = new Button("🔒  Зачыніць нумар");
        closeBtn.getStyleClass().add("hotel-btn-danger");
        closeBtn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty()
                .addListener((o, old, sel) -> closeBtn.setDisable(sel == null));

        closeBtn.setOnAction(e -> {
            Room sel = combo.getValue();
            if (sel == null) return;
            if (!confirm("Зачыніць нумар",
                    "Зачыніць нумар #" + sel.getNumber() + " на тэхнічнае абслугоўванне?")) return;
            try {
                Response resp = send(new Request(Operation.CLOSE_ROOM,
                        mapper.writeValueAsString(sel.getNumber())));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    reloadCombo(combo, msg, false);
                } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) { showError(msg, ex.getMessage()); }
        });

        VBox form = new VBox(10, fieldLabel("Свабодны нумар"), combo, msg, closeBtn);
        form.setMaxWidth(440);
        box.getChildren().add(card("Часова зачыніць нумар", form));
        return box;
    }

    private VBox openRoomView() {
        VBox box = contentBox();
        Label msg = errorLabel();
        ComboBox<Room> combo = buildRoomCombo(msg, true);

        Button openBtn = new Button("🔓  Адчыніць нумар");
        openBtn.getStyleClass().add("hotel-btn-success");
        openBtn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty()
                .addListener((o, old, sel) -> openBtn.setDisable(sel == null));

        openBtn.setOnAction(e -> {
            Room sel = combo.getValue();
            if (sel == null) return;
            try {
                Response resp = send(new Request(Operation.OPEN_ROOM,
                        mapper.writeValueAsString(sel.getNumber())));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    reloadCombo(combo, msg, true);
                } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) { showError(msg, ex.getMessage()); }
        });

        VBox form = new VBox(10, fieldLabel("Нумар на тэхабслугоўванні"), combo, msg, openBtn);
        form.setMaxWidth(440);
        box.getChildren().add(card("Адчыніць нумар", form));
        return box;
    }

    private VBox approvedResView() {
        VBox box = contentBox();
        TableView<Reservation> table = TableHelper.reservationTable();
        table.setPrefHeight(500);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRes(table, msg, Operation.GET_APPROVED_RESERVATIONS));
        box.getChildren().add(card("Зацверджаныя браніраванні",
                new HBox(8, refresh), table, msg));
        loadRes(table, msg, Operation.GET_APPROVED_RESERVATIONS);
        return box;
    }

    private VBox checkOutView() {
        VBox box = contentBox();
        Label msg = errorLabel();

        ComboBox<Reservation> combo = new ComboBox<>();
        combo.getStyleClass().add("hotel-combo");
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setPromptText("Выберыце зацверджанае браніраванне");
        combo.setCellFactory(lv -> resCell());
        combo.setButtonCell(resCell());

        Button checkOutBtn = new Button("🚪  Выселіць і выставіць адзнаку");
        checkOutBtn.getStyleClass().add("hotel-btn-primary");
        checkOutBtn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty()
                .addListener((o, old, sel) -> checkOutBtn.setDisable(sel == null));

        Button refresh = refreshBtn(() -> loadApprovedToCombo(combo, msg));

        checkOutBtn.setOnAction(e -> {
            Reservation sel = combo.getValue();
            if (sel == null) return;
            Optional<Integer> rating = new RatingDialog().show(sel.getId());
            if (rating.isEmpty()) return;
            try {
                CheckOutRequest req = new CheckOutRequest(sel.getId(), rating.get());
                Response resp = send(new Request(Operation.CHECK_OUT,
                        mapper.writeValueAsString(req)));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    combo.setValue(null);
                    loadApprovedToCombo(combo, msg);
                } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) { showError(msg, ex.getMessage()); }
        });

        VBox form = new VBox(12,
                fieldLabel("Зацверджанае браніраванне"), combo,
                new HBox(8, refresh), msg, checkOutBtn);
        form.setMaxWidth(560);
        box.getChildren().add(card("Выселіць госця", form));
        loadApprovedToCombo(combo, msg);
        return box;
    }

    private VBox guestsWithResView() {
        VBox box = contentBox();
        TableView<Guest> table = TableHelper.guestTable();
        table.setPrefHeight(480);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadGuestsWithRes(table, msg));
        box.getChildren().add(card("Госці з браніраваннямі", new HBox(8, refresh), table, msg));
        loadGuestsWithRes(table, msg);
        return box;
    }

    private VBox profileView() {
        VBox box = contentBox();
        var acc = session.getAccount();
        TextField emailFld = field("Email"); emailFld.setText(acc.getEmail());
        TextField firstFld = field("Імя");   firstFld.setText(acc.getFirstName());
        TextField lastFld  = field("Прозвішча"); lastFld.setText(acc.getLastName());
        PasswordField passFld = passField("Новы пароль (абавязкова)");
        Label errLbl = errorLabel();
        Button saveBtn = new Button("Захаваць"); saveBtn.getStyleClass().add("hotel-btn-primary");
        saveBtn.setOnAction(e -> {
            if (passFld.getText().isBlank()) { showError(errLbl, "Увядзіце пароль"); return; }
            try {
                var node = mapper.createObjectNode();
                node.put("accountId", acc.getId());
                node.put("newEmail", emailFld.getText().trim());
                node.put("newFirstName", firstFld.getText().trim());
                node.put("newLastName", lastFld.getText().trim());
                node.put("newPassword", passFld.getText());
                Response resp = send(new Request(Operation.UPDATE_ACCOUNT, mapper.writeValueAsString(node)));
                if (resp != null && resp.isSuccess()) showSuccess(errLbl, resp.getMessage());
                else showError(errLbl, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) { showError(errLbl, ex.getMessage()); }
        });
        VBox form = new VBox(10, fieldLabel("Email"), emailFld,
                fieldLabel("Імя"), firstFld, fieldLabel("Прозвішча"), lastFld,
                fieldLabel("Пароль"), passFld, errLbl, saveBtn);
        form.setMaxWidth(420);
        box.getChildren().add(card("Рэдагаваць акаўнт", form));
        return box;
    }

    private void loadRooms(TableView<Room> table, Label msg, boolean onlyAvail) {
        try {
            Response resp = send(new Request(
                    onlyAvail ? Operation.GET_AVAILABLE_ROOMS : Operation.GET_ALL_ROOMS, null));
            if (resp != null && resp.isSuccess()) {
                table.setItems(FXCollections.observableArrayList(
                        mapper.<List<Room>>readValue(resp.getData(), new TypeReference<>() {})));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) { showError(msg, ex.getMessage()); }
    }

    private void loadRes(TableView<Reservation> table, Label msg, Operation op) {
        try {
            Response resp = send(new Request(op, null));
            if (resp != null && resp.isSuccess()) {
                table.setItems(FXCollections.observableArrayList(
                        mapper.<List<Reservation>>readValue(resp.getData(), new TypeReference<>() {})));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) { showError(msg, ex.getMessage()); }
    }

    private void loadApprovedToCombo(ComboBox<Reservation> combo, Label msg) {
        try {
            Response resp = send(new Request(Operation.GET_APPROVED_RESERVATIONS, null));
            if (resp != null && resp.isSuccess()) {
                combo.setItems(FXCollections.observableArrayList(
                        mapper.<List<Reservation>>readValue(resp.getData(), new TypeReference<>() {})));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) { showError(msg, ex.getMessage()); }
    }

    private void loadGuestsWithRes(TableView<Guest> table, Label msg) {
        try {
            Response resp = send(new Request(Operation.GET_ALL_GUESTS_WITH_RESERVATIONS, null));
            if (resp != null && resp.isSuccess()) {
                List<Guest> list = mapper.readValue(resp.getData(), new TypeReference<>() {});
                table.setItems(FXCollections.observableArrayList(list));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) { showError(msg, ex.getMessage()); }
    }

    private ComboBox<Room> buildRoomCombo(Label msg, boolean maintenanceOnly) {
        ComboBox<Room> combo = new ComboBox<>();
        combo.getStyleClass().add("hotel-combo");
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setPromptText(maintenanceOnly ? "Нумары на тэхабслугоўванні" : "Свабодныя нумары");
        combo.setCellFactory(lv -> roomCell());
        combo.setButtonCell(roomCell());
        reloadCombo(combo, msg, maintenanceOnly);
        return combo;
    }

    private void reloadCombo(ComboBox<Room> combo, Label msg, boolean maintenanceOnly) {
        try {
            Response resp = send(new Request(Operation.GET_ALL_ROOMS, null));
            if (resp != null && resp.isSuccess()) {
                List<Room> all = mapper.readValue(resp.getData(), new TypeReference<>() {});
                combo.setItems(FXCollections.observableArrayList(all.stream()
                        .filter(r -> maintenanceOnly
                                ? r.getStatus() == Room.Status.MAINTENANCE
                                : r.getStatus() == Room.Status.AVAILABLE)
                        .toList()));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) { showError(msg, ex.getMessage()); }
    }

    private ListCell<Room> roomCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Room r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" :
                        "#" + r.getNumber() + " — " + TableHelper.roomTypeStr(r.getType())
                        + " | " + TableHelper.roomStatusStr(r.getStatus()));
            }
        };
    }

    private ListCell<Reservation> resCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" :
                        "#" + r.getId() + " | Нумар " + r.getRoomNumber()
                        + " | Госць " + r.getGuestId()
                        + " | " + r.getReservationDate() + " × " + r.getDuration() + " ноч.");
            }
        };
    }
}
