package com.his.outpatient.dto;

import lombok.Data;

@Data
public class MedicalOrder {
  private Integer id;
  private String kind;
  private Integer registerId;
  private Integer medicalTechnologyId;
  private String info;
  private String position;
}
