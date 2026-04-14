package com.hotel.common.dto;

import com.hotel.common.entities.Employee;

import java.io.Serializable;

public class ChangeRoleRequest implements Serializable {
    private int accountId;
    private Employee.Position newPosition;

    public ChangeRoleRequest() {}

    public int getAccountId() {
        return accountId;
    }

    public Employee.Position getNewPosition() {
        return newPosition;
    }
}
