package com.his.disposal.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DisposalRequestVO {
    private Integer id;
    private Integer registerId;
    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;
    private Integer medicalTechnologyId;
    private String techName;
    private String techFormat;
    private String disposalInfo;
    private String disposalPosition;
    private Date creationTime;
    private Integer disposalEmployeeId;
    private String disposalEmployeeName;
    private Integer inputdisposalEmployeeId;
    private String inputdisposalEmployeeName;
    private Date disposalTime;
    private String disposalResult;
    private String disposalState;
    private Integer paymentReady;
    private String disposalRemark;
}
