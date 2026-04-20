package com.hotel.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RegisterRequest implements Serializable {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String firstName, String lastName,
                           LocalDate birthDate) {
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
    public LocalDate getBirthDate() { return birthDate; }
}