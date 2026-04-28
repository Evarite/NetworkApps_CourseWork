package com.hotel.server.services;

import com.hotel.common.entities.Account;
import com.hotel.server.dao.AccountDao;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

public class AccountService {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Account register(String email, String firstName, String lastName, String password, LocalDate birthDate) {
        if (email == null || email.isBlank())
            throw new RuntimeException("Email пусты");
        if (firstName == null || firstName.isBlank())
            throw new RuntimeException("Імя пустое");
        if (lastName == null || lastName.isBlank())
            throw new RuntimeException("Прозвішча пустое");
        if (password == null || password.isBlank())
            throw new RuntimeException("Пароль пусты");
        if (birthDate == null)
            throw new RuntimeException("Дата нараджэння пустая");
        if (password.length() < 6)
            throw new RuntimeException("Пароль павінен змяшчаць не менш за 6 сімвалаў");

        if (!email.contains("@") || !email.split("@")[1].contains("."))
            throw new RuntimeException("Няправільны фармат email");

        if (accountDao.findByEmail(email) != null)
            throw new RuntimeException("Email ужо заняты");

        String pattern = "[a-zA-Z\\u0400-\\u04FF\u017A\u017C\u0107\u010D\u0144\u0142\u015B\u0161\u016D" +
                "\u017B\u017D\u0106\u010C\u0143\u0141\u015A\u0160\u016C\\s-]+";

        if (!firstName.matches(pattern))
            throw new RuntimeException("Няправільны фармат імя");
        if (!lastName.matches(pattern))
            throw new RuntimeException("Няправільны фармат прозвішча");

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Account account = accountDao.register(email, firstName, lastName, hashedPassword, birthDate);
        if (account == null)
            throw new RuntimeException("Не ўдалося стварыць акаўнт");

        return account;
    }

    public Account login(String email, String password) {
        if (email == null || email.isBlank())
            throw new RuntimeException("Email пусты");
        if (password == null || password.isBlank())
            throw new RuntimeException("Пароль пусты");
        if (!email.contains("@") || !email.split("@")[1].contains("."))
            throw new RuntimeException("Няправільны фармат email");

        Account account = accountDao.login(email, password);
        if (account == null)
            throw new RuntimeException("Няправільны email або пароль");

        return account;
    }

    public void updateAccount(int id, String newEmail, String newFirstName, String newLastName,
                              String newPassword) {
        if (newEmail == null || newEmail.isBlank())
            throw new RuntimeException("Email пусты");
        if (newFirstName == null || newFirstName.isBlank())
            throw new RuntimeException("Імя пустое");
        if (newLastName == null || newLastName.isBlank())
            throw new RuntimeException("Прозвішча пустое");
        if (newPassword == null || newPassword.isBlank())
            throw new RuntimeException("Пароль пусты");
        if (newPassword.length() < 6)
            throw new RuntimeException("Пароль павінен змяшчаць не менш за 6 сімвалаў");

        if (!newEmail.contains("@") || !newEmail.split("@")[1].contains("."))
            throw new RuntimeException("Няправільны фармат email");

        String pattern = "[a-zA-Z\\u0400-\\u04FF\u017A\u017C\u0107\u010D\u0144\u0142\u015B\u0161\u016D" +
                "\u017B\u017D\u0106\u010C\u0143\u0141\u015A\u0160\u016C\\s-]+";

        if (!newFirstName.matches(pattern))
            throw new RuntimeException("Няправільны фармат імя");
        if (!newLastName.matches(pattern))
            throw new RuntimeException("Няправільны фармат прозвішча");

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        accountDao.updateAccount(id, newEmail, newFirstName, newLastName, hashedPassword);
    }
}
