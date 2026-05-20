package com.hotel.client.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hotel.client.components.TableHelper;
import com.hotel.common.dto.LoginResponse;
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

public class ReceptionistDashboard extends ViewBase {

    private final LoginResponse session;

    public ReceptionistDashboard(LoginResponse session) {
        this.session = session;
    }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("hotel-scene");
        root.setTop(buildHeader("Кабінет парцье", session.getAccount(), session.getPosition()));
        root.setLeft(buildSidebar(root));
        root.setCenter(scrollWrap(pendingView()));
        return new Scene(root, 1280, 760);
    }

    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("hotel-sidebar");

        VBox logo = new VBox(2);
        logo.getStyleClass().add("hotel-sidebar-header");
        Label l1 = new Label("🏨 HOTEL");
        l1.getStyleClass().add("hotel-logo-label");
        Label l2 = new Label("Парцье");
        l2.getStyleClass().add("hotel-logo-sub");
        logo.getChildren().addAll(l1, l2);

        Button b1 = sidebarBtn("📋  Чакаюць зацверджання");
        Button b2 = sidebarBtn("🛏  Усе нумары");
        Button b3 = sidebarBtn("✅  Свабодныя нумары");
        Button b4 = sidebarBtn("👥  Госці");
        Button b5 = sidebarBtn("⚙️  Акаўнт");
        Button[] all = {b1, b2, b3, b4, b5};

        b1.setOnAction(e -> {
            activateBtn(b1, all);
            root.setCenter(scrollWrap(pendingView()));
        });
        b2.setOnAction(e -> {
            activateBtn(b2, all);
            root.setCenter(scrollWrap(allRoomsView()));
        });
        b3.setOnAction(e -> {
            activateBtn(b3, all);
            root.setCenter(scrollWrap(availRoomsView()));
        });
        b4.setOnAction(e -> {
            activateBtn(b4, all);
            root.setCenter(scrollWrap(guestsView()));
        });
        b5.setOnAction(e -> {
            activateBtn(b5, all);
            root.setCenter(scrollWrap(profileView()));
        });

        activateBtn(b1, all);
        sidebar.getChildren().addAll(logo,
                sidebarSection("БРАНІРАВАННІ"), b1,
                sidebarSection("НУМАРЫ"), b2, b3,
                sidebarSection("ГОСЦІ"), b4,
                sidebarSection("НАЛАДЫ"), b5);
        return sidebar;
    }

    private VBox pendingView() {
        VBox box = contentBox();
        TableView<Reservation> table = TableHelper.reservationTable();
        table.setPrefHeight(460);
        Label msg = errorLabel();

        Button approveBtn = new Button("✓  Зацвердзіць");
        approveBtn.getStyleClass().add("hotel-btn-success");
        approveBtn.setDisable(true);

        Button cancelBtn = new Button("✗  Скасаваць");
        cancelBtn.getStyleClass().add("hotel-btn-danger");
        cancelBtn.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            boolean isPending = sel != null && sel.getStatus() == Reservation.Status.PENDING;
            approveBtn.setDisable(!isPending);
            cancelBtn.setDisable(sel == null ||
                    sel.getStatus() == Reservation.Status.CANCELLED ||
                    sel.getStatus() == Reservation.Status.CHECKED_OUT);
        });

        approveBtn.setOnAction(e -> {
            Reservation sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (!confirm("Зацверджанне", "Зацвердзіць браніраванне #" + sel.getId() + "?")) return;
            try {
                Response resp = send(new Request(Operation.APPROVE_RESERVATION,
                        mapper.writeValueAsString(sel.getId())));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    loadPending(table, msg);
                } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) {
                showError(msg, ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> {
            Reservation sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (!confirm("Скасаванне", "Скасаваць браніраванне #" + sel.getId() + "?")) return;
            try {
                Response resp = send(new Request(Operation.CANCEL_RESERVATION,
                        mapper.writeValueAsString(sel.getId())));
                if (resp != null && resp.isSuccess()) {
                    showSuccess(msg, resp.getMessage());
                    loadPending(table, msg);
                } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
            } catch (Exception ex) {
                showError(msg, ex.getMessage());
            }
        });

        Button refresh = refreshBtn(() -> loadPending(table, msg));
        HBox toolbar = new HBox(8, refresh, approveBtn, cancelBtn);
        toolbar.getStyleClass().add("hotel-toolbar");

        box.getChildren().add(card("Запыты на браніраванне", toolbar, table, msg));
        loadPending(table, msg);
        return box;
    }

    private VBox allRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(500);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, false));
        box.getChildren().add(card("Усе нумары", new HBox(8, refresh), table, msg));
        loadRooms(table, msg, false);
        return box;
    }

    private VBox availRoomsView() {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable();
        table.setPrefHeight(500);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRooms(table, msg, true));
        box.getChildren().add(card("Свабодныя нумары", new HBox(8, refresh), table, msg));
        loadRooms(table, msg, true);
        return box;
    }

    private VBox guestsView() {
        VBox box = contentBox();
        TableView<com.hotel.common.entities.Guest> table = TableHelper.guestTable();
        table.setPrefHeight(480);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadGuests(table, msg));
        box.getChildren().add(card("Госці гатэля", new HBox(8, refresh), table, msg));
        loadGuests(table, msg);
        return box;
    }

    private VBox profileView() {
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
        Button saveBtn = new Button("Захаваць");
        saveBtn.getStyleClass().add("hotel-btn-primary");
        saveBtn.setOnAction(e -> {
            if (passFld.getText().isBlank()) {
                showError(errLbl, "Для захавання ўвядзіце пароль");
                return;
            }
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
            } catch (Exception ex) {
                showError(errLbl, ex.getMessage());
            }
        });
        VBox form = new VBox(10,
                fieldLabel("Email"), emailFld, fieldLabel("Імя"), firstFld,
                fieldLabel("Прозвішча"), lastFld, fieldLabel("Пароль"), passFld,
                errLbl, saveBtn);
        form.setMaxWidth(420);
        box.getChildren().add(card("Рэдагаваць акаўнт", form));
        return box;
    }

    private void loadPending(TableView<Reservation> table, Label msg) {
        loadReservations(table, msg, Operation.GET_PENDING_RESERVATIONS);
    }

    private void loadRooms(TableView<Room> table, Label msg, boolean onlyAvail) {
        try {
            Response resp = send(new Request(
                    onlyAvail ? Operation.GET_AVAILABLE_ROOMS : Operation.GET_ALL_ROOMS, null));
            if (resp != null && resp.isSuccess()) {
                List<Room> rooms = mapper.readValue(resp.getData(), new TypeReference<>() {
                });
                table.setItems(FXCollections.observableArrayList(rooms));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) {
            showError(msg, ex.getMessage());
        }
    }

    private void loadReservations(TableView<Reservation> table, Label msg, Operation op) {
        try {
            Response resp = send(new Request(op, null));
            if (resp != null && resp.isSuccess()) {
                List<Reservation> list = mapper.readValue(resp.getData(), new TypeReference<>() {
                });
                table.setItems(FXCollections.observableArrayList(list));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) {
            showError(msg, ex.getMessage());
        }
    }

    private void loadGuests(TableView<com.hotel.common.entities.Guest> table, Label msg) {
        try {
            Response resp = send(new Request(Operation.GET_ALL_GUESTS, null));
            if (resp != null && resp.isSuccess()) {
                var list = mapper.readValue(resp.getData(),
                        new TypeReference<List<com.hotel.common.entities.Guest>>() {
                        });
                table.setItems(FXCollections.observableArrayList(list));
                clearMsg(msg);
            } else showError(msg, resp != null ? resp.getMessage() : "Памылка");
        } catch (Exception ex) {
            showError(msg, ex.getMessage());
        }
    }
}
