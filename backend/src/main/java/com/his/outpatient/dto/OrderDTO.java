package com.his.outpatient.dto;

import lombok.Data;

@Data
public class OrderDTO {
  private Integer registerId;
  private Integer medicalTechnologyId;
  private String info;
  private String position;
  // 与现有三类申请实体的字段兼容。
  private String checkInfo;
  private String checkPosition;
  private String inspectionInfo;
  private String inspectionPosition;
  private String disposalInfo;
  private String disposalPosition;
}
