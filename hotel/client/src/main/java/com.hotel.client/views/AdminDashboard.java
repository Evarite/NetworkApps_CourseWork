package com.hotel.client.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hotel.client.components.RatingDialog;
import com.hotel.client.components.TableHelper;
import com.hotel.common.dto.ChangeRoleRequest;
import com.hotel.common.dto.CheckOutRequest;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.entities.*;
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
import java.util.Optional;

public class AdminDashboard extends ViewBase {

    private final LoginResponse session;

    public AdminDashboard(LoginResponse session) { this.session = session; }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("hotel-scene");
        root.setTop(buildHeader("Кабінет адміністратара", session.getAccount(), session.getPosition()));
        root.setLeft(buildSidebar(root));
        root.setCenter(scrollWrap(allRoomsView()));
        return new Scene(root, 1440, 820);
    }

    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("hotel-sidebar");

        VBox logo = new VBox(2);
        logo.getStyleClass().add("hotel-sidebar-header");
        Label l1 = new Label("\uD83C\uDFE8 HOTEL"); l1.getStyleClass().add("hotel-logo-label");
        Label l2 = new Label("Адміністратар"); l2.getStyleClass().add("hotel-logo-sub");
        logo.getChildren().addAll(l1, l2);

        Button bAllRooms   = sidebarBtn("Усе нумары");
        Button bAvailRooms = sidebarBtn("Свабодныя нумары");
        Button bAddRoom    = sidebarBtn("Дадаць нумар");
        Button bDelRoom    = sidebarBtn("Выдаліць нумар");
        Button bCloseRoom  = sidebarBtn("Зачыніць нумар");
        Button bOpenRoom   = sidebarBtn("Адчыніць нумар");
        Button bAllRes     = sidebarBtn("Усе браніраванні");
        Button bPending    = sidebarBtn("Чакаюць зацверджання");
        Button bApprove    = sidebarBtn("Зацвердзіць");
        Button bCancel     = sidebarBtn("Скасаваць");
        Button bApproved   = sidebarBtn("Зацверджаныя");
        Button bCheckOut   = sidebarBtn("Выселіць госця");
        Button bGuests     = sidebarBtn("Усе госці");
        Button bGuestsRes  = sidebarBtn("Госці з браніраваннямі");
        Button bEmployees  = sidebarBtn("Спіс супрацоўнікаў");
        Button bHire       = sidebarBtn("Уладкаваць");
        Button bFire       = sidebarBtn("Звольніць");
        Button bRole       = sidebarBtn("Змяніць пасаду");
        Button bProfile    = sidebarBtn("Мой акаўнт");

        Button[] all = {bAllRooms,bAvailRooms,bAddRoom,bDelRoom,bCloseRoom,bOpenRoom,
            bAllRes,bPending,bApprove,bCancel,bApproved,bCheckOut,
            bGuests,bGuestsRes,bEmployees,bHire,bFire,bRole,bProfile};

        bAllRooms.setOnAction(e->{ activateBtn(bAllRooms,all);   root.setCenter(scrollWrap(allRoomsView())); });
        bAvailRooms.setOnAction(e->{ activateBtn(bAvailRooms,all); root.setCenter(scrollWrap(availRoomsView())); });
        bAddRoom.setOnAction(e->{   activateBtn(bAddRoom,all);   root.setCenter(scrollWrap(addRoomView())); });
        bDelRoom.setOnAction(e->{   activateBtn(bDelRoom,all);   root.setCenter(scrollWrap(deleteRoomView())); });
        bCloseRoom.setOnAction(e->{ activateBtn(bCloseRoom,all); root.setCenter(scrollWrap(closeRoomView())); });
        bOpenRoom.setOnAction(e->{  activateBtn(bOpenRoom,all);  root.setCenter(scrollWrap(openRoomView())); });
        bAllRes.setOnAction(e->{    activateBtn(bAllRes,all);    root.setCenter(scrollWrap(resTableView("Усе браніраванні", Operation.GET_ALL_RESERVATIONS))); });
        bPending.setOnAction(e->{   activateBtn(bPending,all);   root.setCenter(scrollWrap(resTableView("Чакаюць зацверджання", Operation.GET_PENDING_RESERVATIONS))); });
        bApprove.setOnAction(e->{   activateBtn(bApprove,all);   root.setCenter(scrollWrap(approveResView())); });
        bCancel.setOnAction(e->{    activateBtn(bCancel,all);    root.setCenter(scrollWrap(cancelResView())); });
        bApproved.setOnAction(e->{ activateBtn(bApproved,all);  root.setCenter(scrollWrap(resTableView("Зацверджаныя", Operation.GET_APPROVED_RESERVATIONS))); });
        bCheckOut.setOnAction(e->{ activateBtn(bCheckOut,all);  root.setCenter(scrollWrap(checkOutView())); });
        bGuests.setOnAction(e->{    activateBtn(bGuests,all);    root.setCenter(scrollWrap(guestTextView("Усе госці", Operation.GET_ALL_GUESTS))); });
        bGuestsRes.setOnAction(e->{ activateBtn(bGuestsRes,all); root.setCenter(scrollWrap(guestTextView("Госці з браніраваннямі", Operation.GET_ALL_GUESTS_WITH_RESERVATIONS))); });
        bEmployees.setOnAction(e->{ activateBtn(bEmployees,all); root.setCenter(scrollWrap(employeesView())); });
        bHire.setOnAction(e->{      activateBtn(bHire,all);      root.setCenter(scrollWrap(hireView())); });
        bFire.setOnAction(e->{      activateBtn(bFire,all);      root.setCenter(scrollWrap(fireView())); });
        bRole.setOnAction(e->{      activateBtn(bRole,all);      root.setCenter(scrollWrap(changeRoleView())); });
        bProfile.setOnAction(e->{   activateBtn(bProfile,all);   root.setCenter(scrollWrap(profileView())); });

        activateBtn(bAllRooms, all);

        ScrollPane sp = new ScrollPane();
        sp.getStyleClass().add("hotel-scroll");
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox nav = new VBox(0,
            sidebarSection("НУМАРЫ"),       bAllRooms,bAvailRooms,bAddRoom,bDelRoom,bCloseRoom,bOpenRoom,
            sidebarSection("БРАНІРАВАННІ"), bAllRes,bPending,bApprove,bCancel,bApproved,bCheckOut,
            sidebarSection("ГОСЦІ"),        bGuests,bGuestsRes,
            sidebarSection("СУПРАЦОЎНІКІ"), bEmployees,bHire,bFire,bRole,
            sidebarSection("НАЛАДЫ"),       bProfile);
        sp.setContent(nav);
        sp.setPrefHeight(10000);
        sidebar.getChildren().addAll(logo, sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sidebar;
    }

    // ── Room views ────────────────────────────────────────────────────────

    private VBox allRoomsView()   { return roomTableView("Усе нумары", Operation.GET_ALL_ROOMS); }
    private VBox availRoomsView() { return roomTableView("Свабодныя нумары", Operation.GET_AVAILABLE_ROOMS); }

    private VBox roomTableView(String title, Operation op) {
        VBox box = contentBox();
        TableView<Room> table = TableHelper.roomTable(); table.setPrefHeight(520);
        Label msg = errorLabel();
        Button refresh = refreshBtn(() -> loadRoomsToTable(table, msg, op));
        box.getChildren().add(card(title, new HBox(8, refresh), table, msg));
        loadRoomsToTable(table, msg, op);
        return box;
    }

    private VBox addRoomView() {
        VBox box = contentBox();
        Label msg = errorLabel();
        TextField numFld   = field("Нумар (цэлы лік)");
        TextField floorFld = field("Паверх");
        ComboBox<Room.Type> typeCombo = typeCombo();
        ComboBox<Room.Capacity> capCombo = capCombo();
        TextField descFld  = field("Апісанне");
        TextField priceFld = field("Цана за ноч (BYN)");
        Button btn = new Button("Дадаць нумар"); btn.getStyleClass().add("hotel-btn-primary");

        btn.setOnAction(e -> {
            int num, floor; float price;
            try { num   = Integer.parseInt(numFld.getText().trim()); }   catch(NumberFormatException ex){ showError(msg,"Нумар — цэлы лік"); return; }
            try { floor = Integer.parseInt(floorFld.getText().trim()); } catch(NumberFormatException ex){ showError(msg,"Паверх — цэлы лік"); return; }
            if(typeCombo.getValue()==null){ showError(msg,"Выберыце тып"); return; }
            if(capCombo.getValue()==null) { showError(msg,"Выберыце месткасць"); return; }
            try { price = Float.parseFloat(priceFld.getText().trim().replace(",",".")); } catch(NumberFormatException ex){ showError(msg,"Цана — лік"); return; }
            if(price<=0){ showError(msg,"Цана павінна быць станоўчай"); return; }
            try {
                Room room = new Room(num,floor,typeCombo.getValue(),capCombo.getValue(),descFld.getText().trim(),Room.Status.AVAILABLE,price);
                Response resp = send(new Request(Operation.ADD_ROOM, mapper.writeValueAsString(room)));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); numFld.clear();floorFld.clear();descFld.clear();priceFld.clear();typeCombo.setValue(null);capCombo.setValue(null); }
                else showError(msg, resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });

        GridPane grid = new GridPane(); grid.setHgap(16); grid.setVgap(10); grid.setMaxWidth(560);
        ColumnConstraints c1=new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2=new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1,c2);
        grid.addRow(0, vf("Нумар",numFld),    vf("Паверх",floorFld));
        grid.addRow(1, vf("Тып",typeCombo),   vf("Месткасць",capCombo));
        grid.addRow(2, vf("Цана (BYN)",priceFld), new VBox());
        grid.add(vf("Апісанне",descFld),0,3,2,1);

        box.getChildren().add(card("Дадаць новы нумар", grid, msg, btn));
        return box;
    }

    private VBox deleteRoomView() {
        VBox box = contentBox(); Label msg = errorLabel();
        ComboBox<Room> combo = allRoomsCombo(msg);
        Button btn = new Button("Выдаліць нумар"); btn.getStyleClass().add("hotel-btn-danger"); btn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->btn.setDisable(sel==null));
        btn.setOnAction(e->{
            Room sel=combo.getValue(); if(sel==null) return;
            if(!confirm("Выдаліць","Выдаліць нумар #"+sel.getNumber()+"? Нельга адмяніць.")) return;
            try { Response resp=send(new Request(Operation.DELETE_ROOM,mapper.writeValueAsString(sel.getNumber())));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); reloadAllRooms(combo,msg); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(10, fieldLabel("Нумар пакоя"), combo, msg, btn); form.setMaxWidth(440);
        box.getChildren().add(card("Выдаліць нумар", form)); return box;
    }

    private VBox closeRoomView() { return roomActionView("Зачыніць нумар","Зачыніць",Room.Status.AVAILABLE,Operation.CLOSE_ROOM,false); }
    private VBox openRoomView()  { return roomActionView("Адчыніць нумар","Адчыніць",Room.Status.MAINTENANCE,Operation.OPEN_ROOM,true); }

    private VBox roomActionView(String title, String btnText, Room.Status filterStatus, Operation op, boolean success) {
        VBox box = contentBox(); Label msg = errorLabel();
        ComboBox<Room> combo = roomsByStatus(msg, filterStatus);
        Button btn = new Button(btnText); btn.getStyleClass().add(success?"hotel-btn-success":"hotel-btn-danger"); btn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->btn.setDisable(sel==null));
        btn.setOnAction(e->{
            Room sel=combo.getValue(); if(sel==null) return;
            try { Response resp=send(new Request(op,mapper.writeValueAsString(sel.getNumber())));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); reloadByStatus(combo,msg,filterStatus); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(10, fieldLabel("Нумар пакоя"), combo, msg, btn); form.setMaxWidth(440);
        box.getChildren().add(card(title, form)); return box;
    }

    // ── Reservation views ─────────────────────────────────────────────────

    private VBox resTableView(String title, Operation op) {
        VBox box = contentBox();
        TableView<Reservation> table = TableHelper.reservationTable(); table.setPrefHeight(520);
        Label msg = errorLabel();
        Button refresh = refreshBtn(()->loadResToTable(table,msg,op));
        box.getChildren().add(card(title, new HBox(8,refresh), table, msg));
        loadResToTable(table,msg,op); return box;
    }

    private VBox approveResView() { return resActionView("Зацвердзіць браніраванне","Зацвердзіць",true,Operation.GET_PENDING_RESERVATIONS,Operation.APPROVE_RESERVATION); }
    private VBox cancelResView()  { return resActionView("Скасаваць браніраванне","Скасаваць",false,Operation.GET_PENDING_RESERVATIONS,Operation.CANCEL_RESERVATION); }

    private VBox resActionView(String title, String btnText, boolean isSuccess, Operation loadOp, Operation actionOp) {
        VBox box = contentBox(); Label msg = errorLabel();
        ComboBox<Reservation> combo = resCombo(msg, loadOp);
        Button btn = new Button(btnText); btn.getStyleClass().add(isSuccess?"hotel-btn-success":"hotel-btn-danger"); btn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->btn.setDisable(sel==null));
        Button refresh = refreshBtn(()->reloadResCombo(combo,msg,loadOp));
        btn.setOnAction(e->{
            Reservation sel=combo.getValue(); if(sel==null) return;
            if(!confirm(title,btnText+" браніраванне #"+sel.getId()+"?")) return;
            try { Response resp=send(new Request(actionOp,mapper.writeValueAsString(sel.getId())));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); reloadResCombo(combo,msg,loadOp); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(12, fieldLabel("Браніраванне"), combo, new HBox(8,refresh), msg, btn); form.setMaxWidth(540);
        box.getChildren().add(card(title, form)); return box;
    }

    private VBox checkOutView() {
        VBox box = contentBox(); Label msg = errorLabel();
        ComboBox<Reservation> combo = resCombo(msg, Operation.GET_APPROVED_RESERVATIONS);
        Button btn = new Button("Выселіць і выставіць адзнаку"); btn.getStyleClass().add("hotel-btn-primary"); btn.setDisable(true);
        combo.getSelectionModel().selectedItemProperty().addListener((o,old,sel)->btn.setDisable(sel==null));
        Button refresh = refreshBtn(()->reloadResCombo(combo,msg,Operation.GET_APPROVED_RESERVATIONS));
        btn.setOnAction(e->{
            Reservation sel=combo.getValue(); if(sel==null) return;
            Optional<Integer> rating=new RatingDialog().show(sel.getId()); if(rating.isEmpty()) return;
            try { CheckOutRequest req=new CheckOutRequest(sel.getId(),rating.get());
                Response resp=send(new Request(Operation.CHECK_OUT,mapper.writeValueAsString(req)));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); combo.setValue(null); reloadResCombo(combo,msg,Operation.GET_APPROVED_RESERVATIONS); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(12, fieldLabel("Зацверджанае браніраванне"), combo, new HBox(8,refresh), msg, btn); form.setMaxWidth(560);
        box.getChildren().add(card("Выселіць госця", form)); return box;
    }

    // ── Guest views ───────────────────────────────────────────────────────

    private VBox guestTextView(String title, Operation op) {
        VBox box = contentBox();
        TextArea area = new TextArea(); area.setEditable(false); area.setPrefHeight(480);
        area.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");
        Label msg = errorLabel();
        Button refresh = refreshBtn(()->{
            try { Response resp=send(new Request(op,null));
                if(resp!=null&&resp.isSuccess()){ List<Guest> list=mapper.readValue(resp.getData(),new TypeReference<>(){});
                    StringBuilder sb=new StringBuilder();
                    sb.append(String.format("%-12s %-24s%n","ID акаўнта","Сярэдні рэйтынг")).append("─".repeat(40)).append("\n");
                    for(Guest g:list) sb.append(String.format("%-12d %.1f \u2605  (%d адзн.)%n",g.getAccountId(),g.getAverageRating(),g.getReservationsCount()));
                    area.setText(sb.toString());
                } else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        box.getChildren().add(card(title, new HBox(8,refresh), area, msg));
        refresh.fire(); return box;
    }

    // ── Employee views ────────────────────────────────────────────────────

    private VBox employeesView() {
        VBox box = contentBox();
        TextArea area = new TextArea(); area.setEditable(false); area.setPrefHeight(480);
        area.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");
        Label msg = errorLabel();
        Button refresh = refreshBtn(()->loadEmps(area,msg));
        box.getChildren().add(card("Спіс супрацоўнікаў", new HBox(8,refresh), area, msg));
        loadEmps(area,msg); return box;
    }

    private VBox hireView() {
        VBox box = contentBox(); Label msg = errorLabel();
        TextField accId=field("ID акаўнта");
        ComboBox<Employee.Position> posCombo=posCombo();
        TextField salary=field("Заробак (BYN)");
        TextField hireDate=field("Дата прыёму (гггг-мм-дд)");
        Button btn=new Button("Уладкаваць"); btn.getStyleClass().add("hotel-btn-primary");
        btn.setOnAction(e->{
            int id; float sal; LocalDate date;
            try{ id=Integer.parseInt(accId.getText().trim()); } catch(NumberFormatException ex){ showError(msg,"Няправільны ID"); return; }
            if(posCombo.getValue()==null){ showError(msg,"Выберыце пасаду"); return; }
            try{ sal=Float.parseFloat(salary.getText().trim().replace(",",".")); } catch(NumberFormatException ex){ showError(msg,"Заробак — лік"); return; }
            if(sal<=0){ showError(msg,"Заробак павінен быць станоўчым"); return; }
            try{ date=LocalDate.parse(hireDate.getText().trim()); } catch(DateTimeParseException ex){ showError(msg,"Фармат: гггг-мм-дд"); return; }
            try{ Employee emp=new Employee(id,posCombo.getValue(),sal,date);
                Response resp=send(new Request(Operation.HIRE_EMPLOYEE,mapper.writeValueAsString(emp)));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); accId.clear();salary.clear();hireDate.clear();posCombo.setValue(null); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        GridPane grid=new GridPane(); grid.setHgap(16); grid.setVgap(10); grid.setMaxWidth(540);
        ColumnConstraints c1=new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2=new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1,c2);
        grid.addRow(0,vf("ID акаўнта",accId),vf("Пасада",posCombo));
        grid.addRow(1,vf("Заробак (BYN)",salary),vf("Дата прыёму",hireDate));
        box.getChildren().add(card("Уладкаваць супрацоўніка", grid, msg, btn)); return box;
    }

    private VBox fireView() {
        VBox box = contentBox(); Label msg = errorLabel();
        TextArea empArea=new TextArea(); empArea.setEditable(false); empArea.setPrefHeight(160); empArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        loadEmps(empArea,errorLabel());
        TextField accId=field("ID акаўнта для звальнення");
        Button btn=new Button("Звольніць"); btn.getStyleClass().add("hotel-btn-danger");
        btn.setOnAction(e->{
            int id; try{ id=Integer.parseInt(accId.getText().trim()); } catch(NumberFormatException ex){ showError(msg,"Няправільны ID"); return; }
            if(!confirm("Звольненне","Звольніць супрацоўніка з ID "+id+"?")) return;
            try{ Response resp=send(new Request(Operation.FIRE_EMPLOYEE,mapper.writeValueAsString(id)));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); accId.clear(); loadEmps(empArea,errorLabel()); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(10,fieldLabel("Бягучыя супрацоўнікі"),empArea,fieldLabel("ID акаўнта"),accId,msg,btn); form.setMaxWidth(500);
        box.getChildren().add(card("Звольніць супрацоўніка", form)); return box;
    }

    private VBox changeRoleView() {
        VBox box = contentBox(); Label msg = errorLabel();
        TextField accId=field("ID акаўнта");
        ComboBox<Employee.Position> posCombo=posCombo();
        Button btn=new Button("Змяніць пасаду"); btn.getStyleClass().add("hotel-btn-primary");
        btn.setOnAction(e->{
            int id; try{ id=Integer.parseInt(accId.getText().trim()); } catch(NumberFormatException ex){ showError(msg,"Няправільны ID"); return; }
            if(posCombo.getValue()==null){ showError(msg,"Выберыце новую пасаду"); return; }
            try{ ChangeRoleRequest req=new ChangeRoleRequest(id,posCombo.getValue());
                Response resp=send(new Request(Operation.CHANGE_ROLE,mapper.writeValueAsString(req)));
                if(resp!=null&&resp.isSuccess()){ showSuccess(msg,resp.getMessage()); accId.clear();posCombo.setValue(null); }
                else showError(msg,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(msg,ex.getMessage()); }
        });
        VBox form=new VBox(10,fieldLabel("ID акаўнта"),accId,fieldLabel("Новая пасада"),posCombo,msg,btn); form.setMaxWidth(420);
        box.getChildren().add(card("Змяніць пасаду", form)); return box;
    }

    private VBox profileView() {
        VBox box = contentBox(); var acc=session.getAccount();
        TextField emailFld=field("Email"); emailFld.setText(acc.getEmail());
        TextField firstFld=field("Імя");   firstFld.setText(acc.getFirstName());
        TextField lastFld=field("Прозвішча"); lastFld.setText(acc.getLastName());
        PasswordField passFld=passField("Новы пароль (абавязкова)");
        Label errLbl=errorLabel();
        Button saveBtn=new Button("Захаваць"); saveBtn.getStyleClass().add("hotel-btn-primary");
        saveBtn.setOnAction(e->{
            if(passFld.getText().isBlank()){ showError(errLbl,"Увядзіце пароль"); return; }
            try{ var node=mapper.createObjectNode(); node.put("accountId",acc.getId()); node.put("newEmail",emailFld.getText().trim()); node.put("newFirstName",firstFld.getText().trim()); node.put("newLastName",lastFld.getText().trim()); node.put("newPassword",passFld.getText());
                Response resp=send(new Request(Operation.UPDATE_ACCOUNT,mapper.writeValueAsString(node)));
                if(resp!=null&&resp.isSuccess()) showSuccess(errLbl,resp.getMessage()); else showError(errLbl,resp!=null?resp.getMessage():"Памылка");
            } catch(Exception ex){ showError(errLbl,ex.getMessage()); }
        });
        VBox form=new VBox(10,fieldLabel("Email"),emailFld,fieldLabel("Імя"),firstFld,fieldLabel("Прозвішча"),lastFld,fieldLabel("Пароль"),passFld,errLbl,saveBtn); form.setMaxWidth(420);
        box.getChildren().add(card("Рэдагаваць акаўнт", form)); return box;
    }

    // ── Loaders ───────────────────────────────────────────────────────────

    private void loadRoomsToTable(TableView<Room> t, Label msg, Operation op) {
        try{ Response resp=send(new Request(op,null));
            if(resp!=null&&resp.isSuccess()) t.setItems(FXCollections.observableArrayList(mapper.<List<Room>>readValue(resp.getData(),new TypeReference<>(){})));
            else showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ showError(msg,ex.getMessage()); }
    }

    private void loadResToTable(TableView<Reservation> t, Label msg, Operation op) {
        try{ Response resp=send(new Request(op,null));
            if(resp!=null&&resp.isSuccess()) t.setItems(FXCollections.observableArrayList(mapper.<List<Reservation>>readValue(resp.getData(),new TypeReference<>(){})));
            else showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ showError(msg,ex.getMessage()); }
    }

    private void loadEmps(TextArea area, Label msg) {
        try{ Response resp=send(new Request(Operation.GET_ALL_EMPLOYEES,null));
            if(resp!=null&&resp.isSuccess()){ List<Employee> list=mapper.readValue(resp.getData(),new TypeReference<>(){});
                StringBuilder sb=new StringBuilder();
                sb.append(String.format("%-10s %-16s %-10s %-12s%n","ID","Пасада","Заробак","Дата прыёму")).append("─".repeat(52)).append("\n");
                for(Employee emp:list) sb.append(String.format("%-10d %-16s %-10.2f %-12s%n",emp.getAccountId(),posToStr(emp.getPosition()),emp.getSalary(),emp.getHireDate()));
                area.setText(sb.toString());
            } else if(msg!=null) showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ if(msg!=null) showError(msg,ex.getMessage()); }
    }

    // ── Combo factories ───────────────────────────────────────────────────

    private ComboBox<Room> allRoomsCombo(Label msg) {
        ComboBox<Room> combo=new ComboBox<>(); combo.getStyleClass().add("hotel-combo"); combo.setMaxWidth(Double.MAX_VALUE);
        combo.setCellFactory(lv->roomCell()); combo.setButtonCell(roomCell()); reloadAllRooms(combo,msg); return combo;
    }
    private void reloadAllRooms(ComboBox<Room> combo, Label msg) {
        try{ Response resp=send(new Request(Operation.GET_ALL_ROOMS,null));
            if(resp!=null&&resp.isSuccess()) combo.setItems(FXCollections.observableArrayList(mapper.<List<Room>>readValue(resp.getData(),new TypeReference<>(){})));
            else showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ showError(msg,ex.getMessage()); }
    }
    private ComboBox<Room> roomsByStatus(Label msg, Room.Status status) {
        ComboBox<Room> combo=new ComboBox<>(); combo.getStyleClass().add("hotel-combo"); combo.setMaxWidth(Double.MAX_VALUE);
        combo.setCellFactory(lv->roomCell()); combo.setButtonCell(roomCell()); reloadByStatus(combo,msg,status); return combo;
    }
    private void reloadByStatus(ComboBox<Room> combo, Label msg, Room.Status status) {
        try{ Response resp=send(new Request(Operation.GET_ALL_ROOMS,null));
            if(resp!=null&&resp.isSuccess()){ List<Room> all=mapper.readValue(resp.getData(),new TypeReference<>(){});
                combo.setItems(FXCollections.observableArrayList(all.stream().filter(r->r.getStatus()==status).toList()));
            } else showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ showError(msg,ex.getMessage()); }
    }
    private ComboBox<Reservation> resCombo(Label msg, Operation op) {
        ComboBox<Reservation> combo=new ComboBox<>(); combo.getStyleClass().add("hotel-combo"); combo.setMaxWidth(Double.MAX_VALUE);
        combo.setCellFactory(lv->resCell()); combo.setButtonCell(resCell()); reloadResCombo(combo,msg,op); return combo;
    }
    private void reloadResCombo(ComboBox<Reservation> combo, Label msg, Operation op) {
        try{ Response resp=send(new Request(op,null));
            if(resp!=null&&resp.isSuccess()) combo.setItems(FXCollections.observableArrayList(mapper.<List<Reservation>>readValue(resp.getData(),new TypeReference<>(){})));
            else showError(msg,resp!=null?resp.getMessage():"Памылка");
        } catch(Exception ex){ showError(msg,ex.getMessage()); }
    }
    private ComboBox<Room.Type> typeCombo() {
        ComboBox<Room.Type> c=new ComboBox<>(); c.getStyleClass().add("hotel-combo"); c.setMaxWidth(Double.MAX_VALUE); c.getItems().addAll(Room.Type.values());
        c.setCellFactory(lv->new ListCell<>(){ @Override protected void updateItem(Room.Type t,boolean e){ super.updateItem(t,e); setText(e||t==null?"":TableHelper.roomTypeStr(t)); } });
        c.setButtonCell(new ListCell<>(){ @Override protected void updateItem(Room.Type t,boolean e){ super.updateItem(t,e); setText(e||t==null?"Тып":TableHelper.roomTypeStr(t)); } });
        return c;
    }
    private ComboBox<Room.Capacity> capCombo() {
        ComboBox<Room.Capacity> c=new ComboBox<>(); c.getStyleClass().add("hotel-combo"); c.setMaxWidth(Double.MAX_VALUE); c.getItems().addAll(Room.Capacity.values());
        c.setCellFactory(lv->new ListCell<>(){ @Override protected void updateItem(Room.Capacity cap,boolean e){ super.updateItem(cap,e); setText(e||cap==null?"":TableHelper.capacityStr(cap)); } });
        c.setButtonCell(new ListCell<>(){ @Override protected void updateItem(Room.Capacity cap,boolean e){ super.updateItem(cap,e); setText(e||cap==null?"Месткасць":TableHelper.capacityStr(cap)); } });
        return c;
    }
    private ComboBox<Employee.Position> posCombo() {
        ComboBox<Employee.Position> c=new ComboBox<>(); c.getStyleClass().add("hotel-combo"); c.setMaxWidth(Double.MAX_VALUE); c.getItems().addAll(Employee.Position.values());
        c.setCellFactory(lv->new ListCell<>(){ @Override protected void updateItem(Employee.Position p,boolean e){ super.updateItem(p,e); setText(e||p==null?"":posToStr(p)); } });
        c.setButtonCell(new ListCell<>(){ @Override protected void updateItem(Employee.Position p,boolean e){ super.updateItem(p,e); setText(e||p==null?"Пасада":posToStr(p)); } });
        return c;
    }
    private ListCell<Room> roomCell() { return new ListCell<>(){ @Override protected void updateItem(Room r,boolean e){ super.updateItem(r,e); setText(e||r==null?"":"#"+r.getNumber()+" — "+TableHelper.roomTypeStr(r.getType())+" | "+TableHelper.roomStatusStr(r.getStatus())); } }; }
    private ListCell<Reservation> resCell() { return new ListCell<>(){ @Override protected void updateItem(Reservation r,boolean e){ super.updateItem(r,e); setText(e||r==null?"":"#"+r.getId()+" | Нумар "+r.getRoomNumber()+" | Госць "+r.getGuestId()+" | "+r.getReservationDate()+" \u00d7 "+r.getDuration()+" ноч."); } }; }

    private VBox vf(String label, javafx.scene.Node control) { return new VBox(6, fieldLabel(label), control); }
}
