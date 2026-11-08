package com.his.inspection.dto;

import lombok.Data;

import java.util.Date;

@Data
public class InspectionRequestVO {
    private Integer id;
    private Integer registerId;
    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;
    private Integer medicalTechnologyId;
    private String techName;
    private String techFormat;
    private String inspectionInfo;
    private String inspectionPosition;
    private Date creationTime;
    private Integer inspectionEmployeeId;
    private String inspectionEmployeeName;
    private Integer inputinspectionEmployeeId;
    private String inputinspectionEmployeeName;
    private Date inspectionTime;
    private String inspectionResult;
    private String inspectionState;
    private Integer paymentReady;
    private String inspectionRemark;
}
