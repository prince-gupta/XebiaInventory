package com.xebia.services;

import com.xebia.entities.Employee;

/**
 * Created by Pgupta on 13-08-2016.
 */
public interface IEmployeeService {

    public void delete(String eCode, String username);

    public void create(Employee employee, String userName);

    public void update(Employee employee, String userName);
}
