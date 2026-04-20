package com.hotel.server.services;

import com.hotel.common.entities.Account;
import com.hotel.server.dao.AccountDao;

import java.time.LocalDate;

public class AccountService {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Account register(String email, String firstName, String lastName, String password, LocalDate birthDate) {
        if(email == null || email.isBlank())
            throw new RuntimeException("Email пусты");
        if(firstName == null || firstName.isBlank())
            throw new RuntimeException("Імя пустое");
        if(lastName == null || lastName.isBlank())
            throw new RuntimeException("Прозвішча пустое");
        if(password == null || password.isBlank())
            throw new RuntimeException("Пароль пусты");
        if(birthDate == null)
            throw new RuntimeException("Дата нараджэння пустая");

        if(!email.contains("@") || !email.split("@")[1].contains("."))
            throw new RuntimeException("Няправільны фармат email");

        //Both Cyrillic and Latin Belarusian letters
        String pattern = "[a-zA-Z\\u0400-\\u04FFźžćčńłśšŭŹŽĆČŃŁŚŠŬ\\s-]+";

        if(!firstName.matches(pattern))
            throw new RuntimeException("Няправільны фармат імя");

        if(!lastName.matches(pattern))
            throw new RuntimeException("Няправільны фармат прозвішча");

        Account account = accountDao.register(email, firstName, lastName, password, birthDate);
        if(account == null)
            throw new RuntimeException("Няправільны email або пароль");

        return account;
    }
}
