package com.server.entities;

import java.util.Date;

public class Employee {
    public enum Position {
        Receptionist,
        Manager,
        Administrator
    }

    private int accountId;
    private Position position;
    private float salary;
    private Date hireDate;
}
