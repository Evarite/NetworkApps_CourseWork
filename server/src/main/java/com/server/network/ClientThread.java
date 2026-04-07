package com.server.network;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientThread.class);
    private final Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Запушчаны паток для кліента: " + socket);

        try (
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            while (true) {
                Request request = (Request) in.readObject();
                logger.info("Аперацыя: " + request.getOperation());

                Response response = new Response(true, "Запыт апрацаваны", null);

                out.writeObject(response);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("Кліент адключыўся: " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
