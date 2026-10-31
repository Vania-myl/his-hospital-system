package com.his.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 挂号级别表
 */
@Data
public class RegistLevel {
    private Integer id;
    private String registCode;
    private String registName;
    private BigDecimal registFee;
    private Integer registQuota;
    private Integer sequenceNo;
    private Integer delmark;
}
