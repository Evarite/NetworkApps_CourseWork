package com.hotel.common.dto;

import java.io.Serializable;
import java.util.Date;

public class RegisterRequest implements Serializable {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password,
                           String firstName, String lastName, Date birthDate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Date getBirthDate() { return birthDate; }
}