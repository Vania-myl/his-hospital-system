package com.his.entity;

import lombok.Data;

import java.util.Date;

/**
 * 检查申请表
 */
@Data
public class CheckRequest {
    private Integer id;
    private Integer registerId;
    private Integer medicalTechnologyId;
    private String checkInfo;
    private String checkPosition;
    private Date creationTime;
    private Integer checkEmployeeId;
    private Integer inputcheckEmployeeId;
    private Date checkTime;
    private String checkResult;
    private String checkState;
    private String checkRemark;
}
