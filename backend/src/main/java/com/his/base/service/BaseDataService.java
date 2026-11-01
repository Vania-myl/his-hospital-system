package com.his.base.service;

import com.his.entity.Department;
import com.his.entity.Disease;
import com.his.entity.DrugInfo;
import com.his.entity.Employee;
import com.his.entity.MedicalTechnology;
import com.his.entity.RegistLevel;
import com.his.entity.Scheduling;
import com.his.entity.SettleCategory;

import java.util.List;

public interface BaseDataService {

    List<Department> departmentList();

    List<RegistLevel> registLevelList();

    List<SettleCategory> settleCategoryList();

    List<Scheduling> schedulingList();

    List<Employee> employeeList();

    List<MedicalTechnology> medicalTechnologyList();

    List<Disease> diseaseList();

    List<DrugInfo> drugList();
}
