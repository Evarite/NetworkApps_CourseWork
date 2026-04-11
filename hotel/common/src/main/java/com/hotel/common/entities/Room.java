package com.hotel.common.entities;

import java.io.Serializable;

public class Room implements Serializable{
    public enum Type {
        STANDARD,
        SUPERIOR,
        JUNIOR_SUITE,
        SUITE,
        APARTMENTS,
        PRESIDENT
    }
    public enum Capacity {
        SINGLE,
        DOUBLE,
        TWIN,
        TRIPLE,
        FAMILY
    }
    public enum Status {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE
    }

    public Room(int number, int floor, Type type, Capacity capacity, String description,
                Status status, float price) {
        this.number = number;
        this.floor = floor;
        this.type = type;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
        this.price = price;
    }

    private final int number;
    private final int floor;
    private Type type;
    private Capacity capacity;
    private String description;
    private Status status;
    private float price;

    public int getNumber() {
        return number;
    }
    public int getFloor() {
        return floor;
    }
    public Type getType() {
        return type;
    }
    public Capacity getCapacity() {
        return capacity;
    }
    public String getDescription() {
        return description;
    }
    public Status getStatus() {
        return status;
    }
    public float getPrice() {
        return price;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setPrice(float price) {
        this.price = price;
    }
}
