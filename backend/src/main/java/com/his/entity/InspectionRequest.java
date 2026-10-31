package com.his.entity;

import lombok.Data;

import java.util.Date;

/**
 * 检验申请表
 */
@Data
public class InspectionRequest {
    private Integer id;
    private Integer registerId;
    private Integer medicalTechnologyId;
    private String inspectionInfo;
    private String inspectionPosition;
    private Date creationTime;
    private Integer inspectionEmployeeId;
    private Integer inputinspectionEmployeeId;
    private Date inspectionTime;
    private String inspectionResult;
    private String inspectionState;
    private String inspectionRemark;
}
