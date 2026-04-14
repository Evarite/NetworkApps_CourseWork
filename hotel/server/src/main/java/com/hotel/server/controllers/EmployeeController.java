package com.hotel.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.dto.ChangeRoleRequest;
import com.hotel.common.entities.Employee;
import com.hotel.common.network.Request;
import com.hotel.common.network.Response;
import com.hotel.server.dao.EmployeeDao;
import com.hotel.server.exceptions.ResponseException;

public class EmployeeController {
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ObjectMapper mapper = new ObjectMapper();

    public Response getAllEmployees(Request request) {
        try {
            var employees = employeeDao.getAllEmployees();
            String json = mapper.writeValueAsString(employees);
            return new Response(true, "Атрымана супрацоўнікаў: " + employees.size(), json);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response hireEmployee(Request request) {
        try {
            Employee employee = mapper.readValue(request.getData(), Employee.class);
            employeeDao.hireEmployee(employee);
            return new Response(true, "Супрацоўнік уладкаваны паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response fireEmployee(Request request) {
        try {
            int accountId = mapper.readValue(request.getData(), Integer.class);
            employeeDao.fireEmployee(accountId);
            return new Response(true, "Супрацоўнік звольнены паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }

    public Response changeRole(Request request) {
        try {
            ChangeRoleRequest changeRoleRequest = mapper.readValue(request.getData(), ChangeRoleRequest.class);
            employeeDao.changeRole(changeRoleRequest.getAccountId(), changeRoleRequest.getNewPosition());
            return  new Response(true, "Пасада змененая паспяхова", null);
        } catch (Exception e) {
            throw new ResponseException("Памылка падчас запыту: " + e.getMessage());
        }
    }
}
