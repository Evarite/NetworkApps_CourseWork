package com.hotel.common.entities;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {
    public enum Position {
        RECEPTIONIST,
        MANAGER,
        ADMINISTRATOR
    }

    private final int accountId;
    private Position position;
    private float salary;
    private Date hireDate;

    public Employee (int accountId, Position position, float salary, Date hireDate) {
        this.accountId = accountId;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public int getAccountId() {
        return accountId;
    }
    public Position getPosition() {
        return position;
    }
    public float getSalary() {
        return salary;
    }
    public Date getHireDate() {
        return hireDate;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    public void setSalary(float salary) {
        this.salary = salary;
    }
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
}
