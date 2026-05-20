package com.hotel.client.ui;

import com.hotel.client.ui.ThemeManager;
import com.hotel.client.views.*;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.entities.Employee;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage stage;
    private LoginResponse session;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void init(Stage stage) {
        this.stage = stage;
        stage.setTitle("Сістэма кіравання гатэлем");
        stage.setMinWidth(1024);
        stage.setMinHeight(680);
    }

    public void showLogin() {
        session = null;
        LoginView view = new LoginView();
        setScene(view.build(), 480, 600, false);
        stage.centerOnScreen();
    }

    public void onLoginSuccess(LoginResponse lr) {
        this.session = lr;
        showDashboard();
    }

    private void showDashboard() {
        if (session == null) {
            showLogin();
            return;
        }

        Employee.Position pos = session.getPosition();

        Scene scene;
        if (pos == null) {
            scene = new GuestDashboard(session).buildScene();
        } else {
            scene = switch (pos) {
                case RECEPTIONIST -> new ReceptionistDashboard(session).buildScene();
                case MANAGER -> new ManagerDashboard(session).buildScene();
                case ADMINISTRATOR -> new AdminDashboard(session).buildScene();
            };
        }

        setScene(scene, 1280, 760, true);
        stage.centerOnScreen();
    }

    public void logout() {
        showLogin();
    }

    private void setScene(Scene scene, double w, double h, boolean resizable) {
        ThemeManager.getInstance().register(scene);
        stage.setScene(scene);
        stage.setWidth(w);
        stage.setHeight(h);
        stage.setResizable(resizable);
        stage.show();
    }
}
