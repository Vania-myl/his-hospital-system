package com.his.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 医技项目表
 */
@Data
public class MedicalTechnology {
    private Integer id;
    private String techCode;
    private String techName;
    private String techFormat;
    private BigDecimal techPrice;
    private String techType;
    private String priceType;
    private Integer departmentId;
}
