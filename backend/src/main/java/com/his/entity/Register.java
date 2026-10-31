package com.his.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 患者历次挂号信息表
 */
@Data
public class Register {
    private Integer id;
    private String caseNumber;
    private String realName;
    private String gender;
    private String cardNumber;
    private Date birthdate;
    private Integer age;
    private String ageType;
    private String homeAddress;
    private Date visitDate;
    private String noon;
    private Integer departmentId;
    private Integer employeeId;
    private Integer registLevelId;
    private Integer settleCategoryId;
    private String isBook;
    private String registMethod;
    private BigDecimal registMoney;
    private Integer visitState;
}
