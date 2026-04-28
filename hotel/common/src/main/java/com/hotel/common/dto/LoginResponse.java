package com.hotel.common.dto;

import com.hotel.common.entities.Account;
import com.hotel.common.entities.Employee;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private Account account;
    private Employee.Position position;

    public LoginResponse() {}

    public LoginResponse(Account account, Employee.Position position) {
        this.account = account;
        this.position = position;
    }

    public Account getAccount() {
        return account;
    }

    public Employee.Position getPosition() {
        return position;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setPosition(Employee.Position position) {
        this.position = position;
    }
}
