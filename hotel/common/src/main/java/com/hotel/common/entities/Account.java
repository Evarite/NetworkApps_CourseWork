package com.hotel.common.entities;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private final int id;
    private String email;
    private String firstName;
    private String lastName;
    private String password; //Hashed
    private final Date birthDate;

    public Account (int id, String email, String firstName, String lastName, String password, Date birthDate) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Date getBirthDate() {
        return birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
