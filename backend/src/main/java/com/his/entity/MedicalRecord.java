package com.his.entity;

import lombok.Data;

/**
 * 患者病历表
 */
@Data
public class MedicalRecord {
    private Integer id;
    private Integer registerId;
    private String readme;
    private String present;
    private String presentTreat;
    private String history;
    private String allergy;
    private String physique;
    private String proposal;
    private String careful;
    private String diagnosis;
    private String cure;
}
