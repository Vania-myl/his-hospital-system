package com.his.disposal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DisposalPageVO {
    private List<DisposalRequestVO> records;
    private int total;
    private int page;
    private int size;
}
