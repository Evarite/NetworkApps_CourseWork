package com.hotel.client;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.client.network.ServerClient;
import com.hotel.common.entities.Room;
import com.hotel.common.enums.Operation;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;

public class ClientApplication {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        ServerClient serverClient = ServerClient.getInstance();
        serverClient.connect();

        Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_ROOMS, null));

        System.out.println("Адказ ад сервера: " + response.getMessage());
        try {
            List<Room> rooms = mapper.readValue(response.getData(), new TypeReference<List<Room>>() {
            });
            for (Object obj : rooms) {
                System.out.println(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        serverClient.sendRequest(new Request(Operation.DISCONNECT, null));
        serverClient.disconnect();
    }
}
