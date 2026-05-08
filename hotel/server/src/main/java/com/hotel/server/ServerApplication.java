package com.hotel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import com.hotel.server.network.ClientThread;
import org.apache.log4j.Logger;

public class ServerApplication {
    private static final Logger logger = Logger.getLogger(ServerApplication.class);

    public static void main(String[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("server");
        int port = Integer.parseInt(resourceBundle.getString("SERVER_PORT"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запушчаны на порце: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Кліент падлучыўся: " + clientSocket.getInetAddress());

                ClientThread clientThread = new ClientThread(clientSocket);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            logger.error("Падчас працы сервера ўзнікла памылка" + e.getMessage());
        }
    }
}
