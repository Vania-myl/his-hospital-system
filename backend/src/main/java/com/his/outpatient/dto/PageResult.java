package com.his.outpatient.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class PageResult<T> {
  private final List<T> items;
  private final int total;
  private final int page;
  private final int size;

  public PageResult(List<T> items, int total, PageQuery q) {
    this.items = items;
    this.total = total;
    this.page = q.getPage();
    this.size = q.getSize();
  }
}
