package com.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.server.network.ClientThread;
import org.apache.log4j.Logger;

public class ServerApplication {
    private static final Logger logger = Logger.getLogger(ServerApplication.class);

    public static void main(String[] args) {
        int port = 6666;

        try(ServerSocket serverSocket = new ServerSocket(port))
        {
            logger.info("Сервер запушчаны на порце: " + port);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Кліент падлучыўся: " + clientSocket.getInetAddress());

                ClientThread clientThread = new ClientThread(clientSocket);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {

        }
    }
}
