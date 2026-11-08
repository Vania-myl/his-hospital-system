package com.his.check.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CheckRequestVO {
    private Integer id;
    private Integer registerId;
    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;
    private Integer medicalTechnologyId;
    private String techName;
    private String techFormat;
    private String checkInfo;
    private String checkPosition;
    private Date creationTime;
    private Integer checkEmployeeId;
    private String checkEmployeeName;
    private Integer inputcheckEmployeeId;
    private String inputcheckEmployeeName;
    private Date checkTime;
    private String checkResult;
    private String checkState;
    private Integer paymentReady;
    private String checkRemark;
}
