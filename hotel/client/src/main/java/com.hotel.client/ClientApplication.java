package com.hotel.client;

import com.hotel.client.network.ServerClient;
import com.hotel.client.ui.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        ServerClient.getInstance().connect();

        SceneManager.getInstance().init(primaryStage);
        SceneManager.getInstance().showLogin();

        primaryStage.setOnCloseRequest(e -> {
            try {
                ServerClient.getInstance().disconnect();
            } catch (Exception ignored) {
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
