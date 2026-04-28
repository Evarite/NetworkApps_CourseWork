package com.hotel.common.dto;

import com.hotel.common.entities.Employee;

import java.io.Serializable;

public class ChangeRoleRequest implements Serializable {
    private int accountId;
    private Employee.Position newPosition;

    public ChangeRoleRequest() {}

    public ChangeRoleRequest(int accountId, Employee.Position newPosition) {
        this.accountId = accountId;
        this.newPosition = newPosition;
    }

    public int getAccountId() {
        return accountId;
    }

    public Employee.Position getNewPosition() {
        return newPosition;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setNewPosition(Employee.Position newPosition) {
        this.newPosition = newPosition;
    }
}
