package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.dto.ChangeRoleRequest;
import com.hotel.common.entities.Employee;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.exceptions.ResponseException;
import com.hotel.server.services.EmployeeService;

public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeService();
    private final ObjectMapper    mapper          = new ObjectMapper();

    public EmployeeController() {
        mapper.registerModule(new JavaTimeModule());
    }

    public Response getAllEmployees(Request request) {
        try {
            var employees = employeeService.getAllEmployees();
            return new Response(true, "OK", mapper.writeValueAsString(employees));
        } catch (Exception e) {
            throw new ResponseException("Памылка пры чытанні супрацоўнікаў: " + e.getMessage());
        }
    }

    public Response hireEmployee(Request request) {
        try {
            Employee employee = mapper.readValue(request.getData(), Employee.class);
            employeeService.hireEmployee(employee);
            return new Response(true, "Супрацоўнік паспяхова ўладкаваны", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры ўладкаванні: " + e.getMessage());
        }
    }

    public Response fireEmployee(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            employeeService.fireEmployee(accountId);
            return new Response(true, "Супрацоўнік паспяхова звольнены", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры звальненні: " + e.getMessage());
        }
    }

    public Response changeRole(Request request) {
        try {
            ChangeRoleRequest req = mapper.readValue(request.getData(), ChangeRoleRequest.class);
            employeeService.changeRole(req.getAccountId(), req.getNewPosition());
            return new Response(true, "Пасада паспяхова зменена", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка пры змене пасады: " + e.getMessage());
        }
    }
}
