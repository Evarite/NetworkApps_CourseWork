package com.hotel.client.network;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

public class ServerClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final Logger logger = Logger.getLogger(ServerClient.class);

    private static ServerClient instance;

    private ServerClient() {
    }

    public static ServerClient getInstance() {
        if (instance == null)
            instance = new ServerClient();
        return instance;
    }

    public void connect() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("server");
            String host = rb.getString("SERVER_IP");
            int port = Integer.parseInt(rb.getString("SERVER_PORT"));

            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            logger.info("Злучэнне з серверам " + host + ":" + port + " паспяховае");
        } catch (Exception e) {
            logger.error("Не ўдалося злучыцца з серверам: " + e.getMessage());
        }
    }

    public Response sendRequest(Request request) {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            connect();
        }

        if (out == null || in == null) {
            return new Response(false, "Немагчыма злучыцца з серверам. Праверце, ці запушчаны сервер.", null);
        }

        try {
            out.writeObject(request);
            out.flush();
            out.reset();
            return (Response) in.readObject();
        } catch (Exception e) {
            logger.error("Памылка падчас адпраўкі запыту: " + e.getMessage());
            return new Response(false, "Памылка сувязі: " + e.getMessage(), null);
        }
    }

    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}
