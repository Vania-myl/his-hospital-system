package com.his.check.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckPageVO {
    private List<CheckRequestVO> records;
    private int total;
    private int page;
    private int size;
}
