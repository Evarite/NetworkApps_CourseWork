package com.hotel.common.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee implements Serializable {

    public enum Position {
        RECEPTIONIST,
        MANAGER,
        ADMINISTRATOR
    }

    private final int accountId;
    private Position position;
    private float salary;
    private LocalDate hireDate;

    @JsonCreator
    public Employee(
            @JsonProperty("accountId") int accountId,
            @JsonProperty("position") Position position,
            @JsonProperty("salary") float salary,
            @JsonProperty("hireDate") LocalDate hireDate
    ) {
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

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
