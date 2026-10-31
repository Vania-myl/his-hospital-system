package com.his.entity;

import lombok.Data;

/**
 * 医院员工表
 */
@Data
public class Employee {
    private Integer id;
    private String username;
    private String password;
    private String realname;
    private String role;
    private Integer departmentId;
    private Integer registLevelId;
    private Integer schedulingId;
    private Integer delmark;
}
