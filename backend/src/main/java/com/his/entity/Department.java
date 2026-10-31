package com.his.entity;

import lombok.Data;

/**
 * 科室表
 */
@Data
public class Department {
    private Integer id;
    private String deptCode;
    private String deptName;
    private String deptType;
    private Integer delmark;
}
