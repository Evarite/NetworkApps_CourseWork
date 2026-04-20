package com.hotel.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.client.network.ServerClient;
import com.hotel.client.ui.Menu;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;

public class ClientApplication {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        ServerClient serverClient = ServerClient.getInstance();
        serverClient.connect();

        Menu menu = new Menu(serverClient);
        menu.start();

        //Move to menu, I suppose
        serverClient.sendRequest(new Request(Operation.DISCONNECT, null));
        serverClient.disconnect();
    }
}
