package com.his.inspection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InspectionPageVO {
    private List<InspectionRequestVO> records;
    private int total;
    private int page;
    private int size;
}
