package com.hotel.client;

import com.hotel.client.network.ServerClient;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

public class Main {
    public static void main(String[] args) {
        ServerClient serverClient = ServerClient.getInstance();
        serverClient.connect();

        Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_ROOMS, null));

        System.out.println("Адказ ад сервера: " + response.getMessage());

        serverClient.sendRequest(new Request(Operation.DISCONNECT, null));
        serverClient.disconnect();
    }
}
