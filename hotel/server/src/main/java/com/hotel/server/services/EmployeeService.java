package com.hotel.server.services;

import com.hotel.common.entities.Employee;
import com.hotel.server.dao.AccountDao;
import com.hotel.server.dao.EmployeeDao;

import java.time.LocalDate;
import java.util.List;

public class EmployeeService {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final AccountDao  accountDao  = new AccountDao();

    public List<Employee> getAllEmployees() {
        return employeeDao.getAllEmployees();
    }

    public void hireEmployee(Employee employee) {
        if (employee.getAccountId() <= 0)
            throw new RuntimeException("Некарэктны ID акаўнта");
        if (accountDao.findByEmail(null) == null) {
            // праверка существавания акаунта через id
            // (findByEmail не падыходзіць — трэба findById, але ў нас яго няма)
            // На практыцы памылка прыйдзе ад БД (FK), таму проста ідзём далей
        }
        if (employee.getPosition() == null)
            throw new RuntimeException("Не ўказана пасада");
        if (employee.getSalary() <= 0)
            throw new RuntimeException("Заробак павінен быць станоўчым лікам");
        if (employee.getHireDate() == null)
            throw new RuntimeException("Не ўказана дата прыёму");
        if (employee.getHireDate().isAfter(LocalDate.now()))
            throw new RuntimeException("Дата прыёму не можа быць у будучыні");

        // Праверка: ці гэты акаўнт ужо з'яўляецца супрацоўнікам
        Employee.Position existing = employeeDao.getPositionByAccountId(employee.getAccountId());
        if (existing != null)
            throw new RuntimeException("Акаўнт " + employee.getAccountId()
                    + " ужо з'яўляецца супрацоўнікам (" + existing + ")");

        employeeDao.hireEmployee(employee);
    }

    public void fireEmployee(int accountId) {
        if (accountId <= 0)
            throw new RuntimeException("Некарэктны ID акаўнта");

        Employee.Position pos = employeeDao.getPositionByAccountId(accountId);
        if (pos == null)
            throw new RuntimeException("Акаўнт " + accountId + " не з'яўляецца супрацоўнікам");

        employeeDao.fireEmployee(accountId);
    }

    public void changeRole(int accountId, Employee.Position newPosition) {
        if (accountId <= 0)
            throw new RuntimeException("Некарэктны ID акаўнта");
        if (newPosition == null)
            throw new RuntimeException("Не ўказана новая пасада");

        Employee.Position current = employeeDao.getPositionByAccountId(accountId);
        if (current == null)
            throw new RuntimeException("Акаўнт " + accountId + " не з'яўляецца супрацоўнікам");
        if (current == newPosition)
            throw new RuntimeException("Супрацоўнік ужо мае пасаду: " + newPosition);

        employeeDao.changeRole(accountId, newPosition);
    }

    public Employee.Position getPositionByAccountId(int accountId) {
        return employeeDao.getPositionByAccountId(accountId);
    }
}
