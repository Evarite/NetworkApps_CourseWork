package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.network.Response;
import com.hotel.common.network.Request;
import com.hotel.server.dao.UserDao;
import com.hotel.server.exceptions.ResponseException;

import java.util.Date;

public class UserController {
    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response login(Request request) {
        try {
            LoginRequest data = mapper.readValue(request.getData(), LoginRequest.class);
            String email = data.getEmail();
            String password = data.getPassword();

            if(email == null)
                return new Response(false, "Email пакінуты пустым", null);
            if(password == null)
                return new Response(false, "Пароль пакінуты пустым", null);

            Account account = userDao.login(email, password);

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
            Date birthDate = data.getBirthDate();

            if(email == null)
                return new Response(false, "Email пакінуты пустым", null);
            if(password == null)
                return new Response(false, "Пароль пакінуты пустым", null);
            if(firstName == null)
                return new Response(false, "Імя пакінутае пустым", null);
            if(lastName == null)
                return new Response(false, "Прозвішча пакінутае пустым", null);
            if(birthDate == null)
                return new Response(false, "Дата нараджэньня пакінутая пустой", null);

            Account account = userDao.register(email, password, firstName, lastName, birthDate);

            if (account == null) {
                return new Response(false, "Гэты email ужо заняты", null);
            }

            String json = mapper.writeValueAsString(account);
            return new Response(true, "Рэгістрацыя паспяховая", json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас рэгістрацыі: " + e.getMessage());
        }
    }
}
