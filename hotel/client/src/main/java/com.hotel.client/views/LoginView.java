package com.hotel.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.client.ui.SceneManager;
import com.hotel.client.ui.ThemeManager;
import com.hotel.client.network.ServerClient;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LoginView {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ServerClient server = ServerClient.getInstance();

    public LoginView() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Scene build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("hotel-login-pane");

        VBox loginCard = buildLoginCard(root);
        VBox registerCard = buildRegisterCard(root);
        registerCard.setVisible(false);
        registerCard.setManaged(false);

        root.getChildren().addAll(loginCard, registerCard);

        Scene scene = new Scene(root, 480, 600);
        ThemeManager.getInstance().register(scene);

        ((Button) loginCard.lookup("#toRegister")).setOnAction(e -> {
            loginCard.setVisible(false);
            loginCard.setManaged(false);
            registerCard.setVisible(true);
            registerCard.setManaged(true);
        });
        ((Button) registerCard.lookup("#toLogin")).setOnAction(e -> {
            registerCard.setVisible(false);
            registerCard.setManaged(false);
            loginCard.setVisible(true);
            loginCard.setManaged(true);
        });

        return scene;
    }

    private VBox buildLoginCard(StackPane root) {
        VBox card = new VBox(14);
        card.getStyleClass().add("hotel-login-card");
        card.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(card, Pos.CENTER);

        // Кнопка тэмы ўверсе
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Button themeBtn = new Button(ThemeManager.getInstance().themeIcon());
        themeBtn.getStyleClass().add("hotel-theme-btn");
        themeBtn.setOnAction(e -> {
            ThemeManager.getInstance().toggle();
            themeBtn.setText(ThemeManager.getInstance().themeIcon());
        });
        topBar.getChildren().add(themeBtn);

        Label logo = new Label("🏨");
        logo.getStyleClass().add("hotel-login-logo");

        Label title = new Label("Вітаем");
        title.getStyleClass().add("hotel-login-title");

        Label subtitle = new Label("Увайдзіце ў свой ўліковы запіс");
        subtitle.getStyleClass().add("hotel-login-subtitle");

        Label emailLbl = buildFieldLabel("Email");
        TextField emailFld = buildTextField("your@email.com");

        Label passLbl = buildFieldLabel("Пароль");
        PasswordField passFld = buildPassField("••••••");

        Label errLbl = new Label("");
        errLbl.getStyleClass().add("hotel-error-label");
        errLbl.setVisible(false);
        errLbl.setManaged(false);
        errLbl.setWrapText(true);

        Button loginBtn = new Button("Увайсці");
        loginBtn.getStyleClass().add("hotel-btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> doLogin(emailFld.getText().trim(), passFld.getText(), errLbl));
        passFld.setOnAction(e -> loginBtn.fire());

        Region spacer = new Region();
        spacer.setPrefHeight(4);

        HBox registerRow = new HBox(6);
        registerRow.setAlignment(Pos.CENTER);
        registerRow.getChildren().add(new Label("Няма акаўнта?"));
        Button toRegister = new Button("Зарэгістравацца");
        toRegister.setId("toRegister");
        toRegister.getStyleClass().add("hotel-link-btn");
        registerRow.getChildren().add(toRegister);

        card.getChildren().addAll(topBar, logo, title, subtitle,
                emailLbl, emailFld, passLbl, passFld, errLbl, spacer, loginBtn, registerRow);
        return card;
    }

    private VBox buildRegisterCard(StackPane root) {
        VBox card = new VBox(12);
        card.getStyleClass().add("hotel-login-card");
        card.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(card, Pos.CENTER);

        Label logo = new Label("🏨");
        logo.getStyleClass().add("hotel-login-logo");

        Label title = new Label("Рэгістрацыя");
        title.getStyleClass().add("hotel-login-title");

        Label subtitle = new Label("Стварыце ўліковы запіс");
        subtitle.getStyleClass().add("hotel-login-subtitle");

        TextField emailFld = buildTextField("Email");
        TextField firstFld = buildTextField("Імя");
        TextField lastFld = buildTextField("Прозвішча");
        PasswordField passFld = buildPassField("Пароль (мін. 6 сімвалаў)");
        TextField birthFld = buildTextField("Дата нараджэння (гггг-мм-дд)");

        Label errLbl = new Label("");
        errLbl.getStyleClass().add("hotel-error-label");
        errLbl.setVisible(false);
        errLbl.setManaged(false);
        errLbl.setWrapText(true);

        Button regBtn = new Button("Зарэгістравацца");
        regBtn.getStyleClass().add("hotel-btn-primary");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setOnAction(e -> doRegister(
                emailFld.getText().trim(), firstFld.getText().trim(),
                lastFld.getText().trim(), passFld.getText(),
                birthFld.getText().trim(), errLbl));

        HBox backRow = new HBox(6);
        backRow.setAlignment(Pos.CENTER);
        backRow.getChildren().add(new Label("Ужо зарэгістраваны?"));
        Button toLogin = new Button("Увайсці");
        toLogin.setId("toLogin");
        toLogin.getStyleClass().add("hotel-link-btn");
        backRow.getChildren().add(toLogin);

        card.getChildren().addAll(logo, title, subtitle,
                buildFieldLabel("Email"), emailFld,
                buildFieldLabel("Імя"), firstFld,
                buildFieldLabel("Прозвішча"), lastFld,
                buildFieldLabel("Пароль"), passFld,
                buildFieldLabel("Дата нараджэння"), birthFld,
                errLbl, regBtn, backRow);
        return card;
    }

    private void doLogin(String email, String password, Label errLbl) {
        if (email.isBlank() || password.isBlank()) {
            show(errLbl, "Запоўніце ўсе палі");
            return;
        }
        try {
            String json = mapper.writeValueAsString(new LoginRequest(email, password));
            Response resp = server.sendRequest(new Request(Operation.LOGIN, json));
            if (resp == null || !resp.isSuccess()) {
                show(errLbl, resp == null ? "Сервер недаступны" : resp.getMessage());
                return;
            }
            LoginResponse lr = mapper.readValue(resp.getData(), LoginResponse.class);
            SceneManager.getInstance().onLoginSuccess(lr);
        } catch (Exception ex) {
            show(errLbl, "Памылка: " + ex.getMessage());
        }
    }

    private void doRegister(String email, String first, String last,
                            String pass, String birthStr, Label errLbl) {
        if (email.isBlank() || first.isBlank() || last.isBlank() || pass.isBlank() || birthStr.isBlank()) {
            show(errLbl, "Запоўніце ўсе палі");
            return;
        }
        LocalDate birth;
        try {
            birth = LocalDate.parse(birthStr);
        } catch (DateTimeParseException e) {
            show(errLbl, "Няправільны фармат даты. Выкарыстоўвайце гггг-мм-дд");
            return;
        }
        try {
            RegisterRequest req = new RegisterRequest(email, pass, first, last, birth);
            String json = mapper.writeValueAsString(req);
            Response resp = server.sendRequest(new Request(Operation.REGISTER, json));
            if (resp == null || !resp.isSuccess()) {
                show(errLbl, resp == null ? "Сервер недаступны" : resp.getMessage());
                return;
            }
            LoginResponse lr = mapper.readValue(resp.getData(), LoginResponse.class);
            SceneManager.getInstance().onLoginSuccess(lr);
        } catch (Exception ex) {
            show(errLbl, "Памылка: " + ex.getMessage());
        }
    }

    private Label buildFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("hotel-field-label");
        return lbl;
    }

    private TextField buildTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("hotel-text-field");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private PasswordField buildPassField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.getStyleClass().add("hotel-text-field");
        pf.setMaxWidth(Double.MAX_VALUE);
        return pf;
    }

    private void show(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
