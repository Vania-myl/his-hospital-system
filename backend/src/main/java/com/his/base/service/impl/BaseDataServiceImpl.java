package com.his.base.service.impl;

import com.his.base.service.BaseDataService;
import com.his.entity.Department;
import com.his.entity.Employee;
import com.his.entity.RegistLevel;
import com.his.entity.Scheduling;
import com.his.entity.SettleCategory;
import com.his.mapper.DepartmentMapper;
import com.his.mapper.EmployeeMapper;
import com.his.mapper.RegistLevelMapper;
import com.his.mapper.SchedulingMapper;
import com.his.mapper.SettleCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseDataServiceImpl implements BaseDataService {

    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private RegistLevelMapper registLevelMapper;
    @Autowired
    private SettleCategoryMapper settleCategoryMapper;
    @Autowired
    private SchedulingMapper schedulingMapper;
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<Department> departmentList() {
        return departmentMapper.selectActive();
    }

    @Override
    public List<RegistLevel> registLevelList() {
        return registLevelMapper.selectActive();
    }

    @Override
    public List<SettleCategory> settleCategoryList() {
        return settleCategoryMapper.selectActive();
    }

    @Override
    public List<Scheduling> schedulingList() {
        return schedulingMapper.selectActive();
    }

    @Override
    public List<Employee> employeeList() {
        return employeeMapper.selectActive();
    }
}
