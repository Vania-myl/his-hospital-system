package com.his.entity;

import lombok.Data;

/**
 * 疾病表
 */
@Data
public class Disease {
    private Integer id;
    private String diseaseCode;
    private String diseaseName;
    private String diseaseIcd;
    private String diseaseCategory;
}
