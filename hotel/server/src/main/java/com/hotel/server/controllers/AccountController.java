package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.network.Response;
import com.hotel.common.network.Request;
import com.hotel.server.dao.AccountDao;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.AccountService;

import java.time.LocalDate;

public class AccountController {
    private final AccountDao accountDao = new AccountDao();
    private final AccountService accountService = new AccountService(accountDao);
    private final ObjectMapper mapper = new ObjectMapper();

    public AccountController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response login(Request request) {
        try {
            LoginRequest data = mapper.readValue(request.getData(), LoginRequest.class);
            String email = data.getEmail();
            String password = data.getPassword();

            if(email == null)
                return new Response(false, "Email пакінуты пустым", null);
            if(password == null)
                return new Response(false, "Пароль пакінуты пустым", null);

            Account account = accountDao.login(email, password);
            if(account == null)
                return new Response(false, "Няправільны пароль або імя акаўнта", null);

            String json = mapper.writeValueAsString(account);
            return new Response(true, "Уваход пасьпяховы", json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас уваходу: " + e.getMessage());
        }
    }

    public Response register(Request request) {
        try {
            RegisterRequest data = mapper.readValue(request.getData(), RegisterRequest.class);
            String email = data.getEmail();
            String password = data.getPassword();
            String firstName = data.getFirstName();
            String lastName = data.getLastName();
            LocalDate birthDate = data.getBirthDate();

            Account account = accountService.register(email, firstName, lastName, password, birthDate);

            String json = mapper.writeValueAsString(account);
            return new Response(true, "Рэгістрацыя паспяховая", json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас рэгістрацыі: " + e.getMessage());
        }
    }
}
