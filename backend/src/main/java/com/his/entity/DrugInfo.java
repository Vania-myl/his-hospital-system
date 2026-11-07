package com.his.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 药品信息表
 */
@Data
public class DrugInfo {
    private Integer id;
    private String drugCode;
    private String drugName;
    private String drugFormat;
    private String drugUnit;
    private String manufacturer;
    private String drugDosage;
    private String drugType;
    private BigDecimal drugPrice;
    private String mnemonicCode;
    private Date creationDate;
    private Integer delmark;
    private Integer stockQuantity;
}
