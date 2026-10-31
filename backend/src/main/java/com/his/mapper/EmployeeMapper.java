package com.his.mapper;

import com.his.entity.Employee;

import java.util.List;

public interface EmployeeMapper {

    Employee selectByUsername(String username);

    List<Employee> selectActive();
}
