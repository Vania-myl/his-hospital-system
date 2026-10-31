package com.his.mapper;

import com.his.entity.Department;

import java.util.List;

public interface DepartmentMapper {

    List<Department> selectActive();
}
