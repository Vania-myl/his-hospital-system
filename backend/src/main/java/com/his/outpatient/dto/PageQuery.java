package com.his.outpatient.dto;

import lombok.Data;

@Data
public class PageQuery {
  private int page = 1;
  private int size = 20;
  private String keyword;
  private String date;
  private String noon;
  private Integer visitState;
  private String drugState;
  private Boolean paid;
  private Integer registerId;
  private String type;

  public int getOffset() {
    return (getPage() - 1) * getSize();
  }

  public int getPage() {
    return Math.max(1, Math.min(page, 100000));
  }

  public int getSize() {
    return Math.max(1, Math.min(size, 100));
  }
}
