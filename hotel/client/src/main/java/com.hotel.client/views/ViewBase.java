package com.hotel.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.client.ui.SceneManager;
import com.hotel.client.ui.ThemeManager;
import com.hotel.client.network.ServerClient;
import com.hotel.common.entities.Account;
import com.hotel.common.entities.Employee;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public abstract class ViewBase {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ServerClient server = ServerClient.getInstance();

    protected ViewBase() {
        mapper.registerModule(new JavaTimeModule());
    }

    protected HBox buildHeader(String title, Account account, Employee.Position position) {
        HBox header = new HBox();
        header.getStyleClass().add("hotel-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("hotel-page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label roleBadge = new Label(posToStr(position));
        roleBadge.getStyleClass().add("hotel-user-badge");

        Label userLbl = new Label(account.getFirstName() + " " + account.getLastName());
        userLbl.getStyleClass().add("hotel-text-muted");

        Button themeBtn = new Button(ThemeManager.getInstance().themeIcon());
        themeBtn.getStyleClass().add("hotel-theme-btn");
        themeBtn.setOnAction(e -> {
            ThemeManager.getInstance().toggle();
            themeBtn.setText(ThemeManager.getInstance().themeIcon());
        });

        Button logoutBtn = new Button("Выйсці");
        logoutBtn.getStyleClass().add("hotel-logout-btn");
        logoutBtn.setOnAction(e -> SceneManager.getInstance().logout());

        header.getChildren().addAll(titleLbl, spacer, roleBadge, userLbl, themeBtn, logoutBtn);
        return header;
    }

    protected Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("hotel-nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    protected Label sidebarSection(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("hotel-sidebar-section");
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    protected void activateBtn(Button active, Button... all) {
        for (Button b : all) b.getStyleClass().remove("active");
        active.getStyleClass().add("active");
    }

    protected ScrollPane scrollWrap(Pane content) {
        ScrollPane sp = new ScrollPane(content);
        sp.getStyleClass().add("hotel-scroll");
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    protected VBox contentBox() {
        VBox box = new VBox(20);
        box.getStyleClass().add("hotel-content");
        box.setFillWidth(true);
        return box;
    }

    protected VBox card(String title, javafx.scene.Node... children) {
        VBox card = new VBox(10);
        card.getStyleClass().add("hotel-card");
        if (title != null) {
            Label t = new Label(title);
            t.getStyleClass().add("hotel-card-title");
            card.getChildren().add(t);
        }
        card.getChildren().addAll(children);
        return card;
    }

    protected TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("hotel-text-field");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    protected PasswordField passField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.getStyleClass().add("hotel-text-field");
        pf.setMaxWidth(Double.MAX_VALUE);
        return pf;
    }

    protected Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("hotel-field-label");
        return lbl;
    }

    protected Label errorLabel() {
        Label lbl = new Label("");
        lbl.getStyleClass().add("hotel-error-label");
        lbl.setWrapText(true);
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    protected void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.getStyleClass().removeAll("hotel-success-label");
        lbl.getStyleClass().add("hotel-error-label");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    protected void showSuccess(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.getStyleClass().removeAll("hotel-error-label");
        lbl.getStyleClass().add("hotel-success-label");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    protected void clearMsg(Label lbl) {
        lbl.setVisible(false);
        lbl.setManaged(false);
    }

    protected Response send(Request req) {
        try {
            return server.sendRequest(req);
        } catch (Exception e) {
            return new Response(false, "Немагчыма злучыцца з серверам: " + e.getMessage(), null);
        }
    }

    protected void alert(String title, String msg, Alert.AlertType type) {
        Alert al = new Alert(type);
        al.setTitle(title);
        al.setHeaderText(null);
        al.setContentText(msg);
        al.showAndWait();
    }

    protected boolean confirm(String title, String msg) {
        Alert al = new Alert(Alert.AlertType.CONFIRMATION);
        al.setTitle(title);
        al.setHeaderText(null);
        al.setContentText(msg);
        return al.showAndWait().map(r -> r == ButtonType.OK).orElse(false);
    }

    protected String posToStr(Employee.Position pos) {
        if (pos == null) return "Госць";
        return switch (pos) {
            case RECEPTIONIST -> "Парцье";
            case MANAGER -> "Мэнэджар";
            case ADMINISTRATOR -> "Адміністратар";
        };
    }

    protected Pane sep() {
        Pane p = new Pane();
        p.getStyleClass().add("hotel-sep");
        p.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(p, new Insets(4, 0, 4, 0));
        return p;
    }

    protected Button refreshBtn(Runnable action) {
        Button btn = new Button("↻ Аднавіць");
        btn.getStyleClass().add("hotel-btn-secondary");
        btn.setOnAction(e -> action.run());
        return btn;
    }
}
