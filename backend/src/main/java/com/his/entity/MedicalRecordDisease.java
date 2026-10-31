package com.his.entity;

import lombok.Data;

/**
 * 病历-疾病关联表
 */
@Data
public class MedicalRecordDisease {
    private Integer medicalRecordId;
    private Integer diseaseId;
}
