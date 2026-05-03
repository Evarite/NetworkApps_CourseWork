package com.hotel.client.network;

import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

import java.io.*;
import java.net.Socket;

/**
 * Сінглтон для злучэння з серверам.
 * Замяняе стары ServerClient.java з поўным апрацоўваннем выключэнняў.
 */
public class ServerClient {

    private static final ServerClient INSTANCE = new ServerClient();

    private static final String HOST = "localhost";
    private static final int    PORT = 8080;

    private Socket           socket;
    private ObjectOutputStream out;
    private ObjectInputStream  in;

    private ServerClient() {}

    public static ServerClient getInstance() { return INSTANCE; }

    public void connect() {
        try {
            socket = new Socket(HOST, PORT);
            out    = new ObjectOutputStream(socket.getOutputStream());
            in     = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Не ўдалося злучыцца з серверам: " + e.getMessage());
            // Не кідаем выключэнне — дазваляем GUI запусціцца,
            // памылка з'явіцца пры першым запыце
        }
    }

    public Response sendRequest(Request request) {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            // Паспрабуем перазлучыцца
            try { connect(); } catch (Exception ignored) {}
        }
        if (socket == null) {
            return new Response(false, "Немагчыма злучыцца з серверам", null);
        }
        try {
            out.writeObject(request);
            out.flush();
            out.reset(); // прадухіляе кэшаванне аб'ектаў
            return (Response) in.readObject();
        } catch (Exception e) {
            return new Response(false, "Памылка сувязі: " + e.getMessage(), null);
        }
    }

    public void disconnect() {
        try {
            if (out    != null) out.close();
            if (in     != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
