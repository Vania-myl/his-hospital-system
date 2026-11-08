package com.his.record.dto;

import lombok.Data;

import java.util.List;

/**
 * 历史业务查询分页结果
 */
@Data
public class RecordHistoryPageVO {

    private List<RecordHistoryVO> list;
    private Integer total;
}
