package com.his.entity;

import lombok.Data;

import java.util.Date;

/**
 * 处方表
 */
@Data
public class Prescription {
    private Integer id;
    private Integer registerId;
    private Integer drugId;
    private String drugUsage;
    private String drugNumber;
    private Date creationTime;
    private String drugState;
}
