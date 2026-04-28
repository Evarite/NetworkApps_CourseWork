package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.dto.LoginRequest;
import com.hotel.common.dto.LoginResponse;
import com.hotel.common.dto.RegisterRequest;
import com.hotel.common.entities.Account;
import com.hotel.common.entities.Employee;
import com.hotel.common.network.Response;
import com.hotel.common.network.Request;
import com.hotel.server.dao.AccountDao;
import com.hotel.server.dao.EmployeeDao;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.AccountService;

import java.time.LocalDate;

public class AccountController {
    private final AccountDao accountDao = new AccountDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final AccountService accountService = new AccountService(accountDao);
    private final ObjectMapper mapper = new ObjectMapper();

    public AccountController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response login(Request request) {
        try {
            LoginRequest data = mapper.readValue(request.getData(), LoginRequest.class);
            Account account = accountService.login(data.email(), data.password());

            Employee.Position position = employeeDao.getPositionByAccountId(account.getId());

            LoginResponse loginResponse = new LoginResponse(account, position);
            String json = mapper.writeValueAsString(loginResponse);
            return new Response(true, "Уваход паспяховы", json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас уваходу: " + e.getMessage());
        }
    }

    public Response register(Request request) {
        try {
            RegisterRequest data = mapper.readValue(request.getData(), RegisterRequest.class);
            Account account = accountService.register(
                    data.getEmail(), data.getFirstName(), data.getLastName(),
                    data.getPassword(), data.getBirthDate()
            );

            LoginResponse loginResponse = new LoginResponse(account, null);
            String json = mapper.writeValueAsString(loginResponse);
            return new Response(true, "Рэгістрацыя паспяховая", json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас рэгістрацыі: " + e.getMessage());
        }
    }

    public Response updateAccount(Request request) {
        try {

            UpdateAccountRequest data = mapper.readValue(request.getData(), UpdateAccountRequest.class);
            accountService.updateAccount(
                    data.getAccountId(),
                    data.getNewEmail(),
                    data.getNewFirstName(),
                    data.getNewLastName(),
                    data.getNewPassword()
            );
            return new Response(true, "Акаўнт паспяхова абноўлены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас абнаўлення акаўнта: " + e.getMessage());
        }
    }

    public static class UpdateAccountRequest implements java.io.Serializable {
        private int accountId;
        private String newEmail;
        private String newFirstName;
        private String newLastName;
        private String newPassword;

        public UpdateAccountRequest() {}

        public int getAccountId() { return accountId; }
        public String getNewEmail() { return newEmail; }
        public String getNewFirstName() { return newFirstName; }
        public String getNewLastName() { return newLastName; }
        public String getNewPassword() { return newPassword; }
    }
}
