package com.his.pharmacy.dto;

import lombok.Data;

@Data
public class StockDTO {
  private String type;
  private Integer quantity;
  private String reason;
}
