package com.hotel.server.services;

import com.hotel.common.entities.Room;
import com.hotel.server.dao.RoomDao;

import java.util.List;

public class RoomService {

    private final RoomDao roomDao = new RoomDao();

    public List<Room> getAllRooms() {
        return roomDao.getAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return roomDao.getAvailableRooms();
    }

    public void addRoom(Room room) {
        validateRoom(room);
        roomDao.addRoom(room);
    }

    public void updateRoom(Room room) {
        validateRoom(room);
        roomDao.updateRoom(room);
    }

    public void deleteRoom(int number) {
        if (number <= 0)
            throw new RuntimeException("Некарэктны нумар пакоя");
        roomDao.deleteRoom(number);
    }

    public void closeRoom(int number) {
        if (number <= 0)
            throw new RuntimeException("Некарэктны нумар пакоя");
        roomDao.closeRoom(number);
    }

    public void openRoom(int number) {
        if (number <= 0)
            throw new RuntimeException("Некарэктны нумар пакоя");
        roomDao.openRoom(number);
    }

    private void validateRoom(Room room) {
        if (room == null)
            throw new RuntimeException("Дадзеныя пакоя адсутнічаюць");
        if (room.getNumber() <= 0)
            throw new RuntimeException("Нумар пакоя павінен быць станоўчым цэлым лікам");
        if (room.getFloor() <= 0)
            throw new RuntimeException("Паверх павінен быць станоўчым цэлым лікам");
        if (room.getFloor() > 100)
            throw new RuntimeException("Паверх не можа перавышаць 100");
        if (room.getType() == null)
            throw new RuntimeException("Не ўказаны тып нумара");
        if (room.getCapacity() == null)
            throw new RuntimeException("Не ўказана месткасць");
        if (room.getPrice() <= 0)
            throw new RuntimeException("Кошт за ноч павінен быць станоўчым лікам");
        if (room.getPrice() > 100_000)
            throw new RuntimeException("Кошт за ноч не можа перавышаць 100 000 BYN");
    }
}
