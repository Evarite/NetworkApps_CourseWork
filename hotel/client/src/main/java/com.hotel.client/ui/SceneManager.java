package com.hotel.client.ui;

import com.hotel.client.views.*;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.entities.Employee;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Кіруе навігацыяй паміж экранамі.
 * Утрымлівае спасылку на Stage і бягучую сесію.
 */
public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage stage;
    private LoginResponse session; // бягучая сесія

    private SceneManager() {}

    public static SceneManager getInstance() { return INSTANCE; }

    public void init(Stage stage) {
        this.stage = stage;
        stage.setTitle("Сістэма кіравання гатэлем");
        stage.setMinWidth(1024);
        stage.setMinHeight(680);
    }

    public LoginResponse getSession() { return session; }

    /** Паказвае экран уваходу. */
    public void showLogin() {
        session = null;
        LoginView view = new LoginView();
        setScene(view.build(), 480, 600, false);
        stage.centerOnScreen();
    }

    /** Выклікаецца пасля паспяховага ўваходу/рэгістрацыі. */
    public void onLoginSuccess(LoginResponse lr) {
        this.session = lr;
        showDashboard();
    }

    /** Накіроўвае ў патрэбны дашборд паводле ролі. */
    private void showDashboard() {
        if (session == null) { showLogin(); return; }

        Employee.Position pos = session.getPosition();

        Scene scene;
        if (pos == null) {
            scene = new GuestDashboard(session).buildScene();
        } else {
            scene = switch (pos) {
                case RECEPTIONIST  -> new ReceptionistDashboard(session).buildScene();
                case MANAGER       -> new ManagerDashboard(session).buildScene();
                case ADMINISTRATOR -> new AdminDashboard(session).buildScene();
            };
        }

        setScene(scene, 1280, 760, true);
        stage.centerOnScreen();
    }

    /** Выйсці — вяртаемся на экран уваходу. */
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
