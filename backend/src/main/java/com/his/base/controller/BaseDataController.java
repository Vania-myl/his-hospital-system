package com.his.base.controller;

import com.his.base.service.BaseDataService;
import com.his.common.Result;
import com.his.entity.Department;
import com.his.entity.Employee;
import com.his.entity.RegistLevel;
import com.his.entity.Scheduling;
import com.his.entity.SettleCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BaseDataController {

    @Autowired
    private BaseDataService baseDataService;

    @GetMapping("/department/list")
    public Result<List<Department>> departmentList() {
        return Result.success(baseDataService.departmentList());
    }

    @GetMapping("/regist-level/list")
    public Result<List<RegistLevel>> registLevelList() {
        return Result.success(baseDataService.registLevelList());
    }

    @GetMapping("/settle-category/list")
    public Result<List<SettleCategory>> settleCategoryList() {
        return Result.success(baseDataService.settleCategoryList());
    }

    @GetMapping("/scheduling/list")
    public Result<List<Scheduling>> schedulingList() {
        return Result.success(baseDataService.schedulingList());
    }

    @GetMapping("/employee/list")
    public Result<List<Employee>> employeeList() {
        return Result.success(baseDataService.employeeList());
    }
}
