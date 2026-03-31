package com.server.entities;

public class Room {
    public enum Type {
        Standard,
        Superior,
        JuniorSuite,
        Suite,
        Apartments,
        President
    }
    public enum Capacity {
        Single,
        Double,
        Twin,
        Triple,
        Family
    }
    public enum Status {
        Available,
        Occupied,
        Maintenance
    }

    private int number;
    private int floor;
    private Type type;
    private Capacity capacity;
    private String description;
    private Status status;
    private float price;
}
