package com.hotel.client.network;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;
import java.util.ResourceBundle;

public class ServerClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final Logger logger = Logger.getLogger(ServerClient.class);

    private static ServerClient instance;
    private ServerClient() {}

    public static ServerClient getInstance() {
        if(instance == null)
            instance = new ServerClient();
        return instance;
    }

    public void connect() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("server");
            String host = resourceBundle.getString("SERVER_IP");
            int port = Integer.parseInt(resourceBundle.getString("SERVER_PORT"));

            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            logger.error("Узнікла памылка падчас падлучэння: " + e.getMessage());
            System.out.println("Узнікла памылка падчас падлучэння.");
        }
    }

    public Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();

            return (Response)in.readObject();
        } catch (Exception e) {
            logger.error("Узнікла памылка падчас адпраўкі запыту: " + e.getMessage());
            System.out.println("Узнікла памылка падчас адпраўкі запыту.");
        }
        return null;
    }

    public void disconnect() {
        try {
            if (socket != null)
                socket.close();
        } catch (Exception ignored) {
        }
    }
}
