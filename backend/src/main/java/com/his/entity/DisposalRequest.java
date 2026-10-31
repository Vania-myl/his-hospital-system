package com.his.entity;

import lombok.Data;

import java.util.Date;

/**
 * 处置申请表
 */
@Data
public class DisposalRequest {
    private Integer id;
    private Integer registerId;
    private Integer medicalTechnologyId;
    private String disposalInfo;
    private String disposalPosition;
    private Date creationTime;
    private Integer disposalEmployeeId;
    private Integer inputdisposalEmployeeId;
    private Date disposalTime;
    private String disposalResult;
    private String disposalState;
    private String disposalRemark;
}
