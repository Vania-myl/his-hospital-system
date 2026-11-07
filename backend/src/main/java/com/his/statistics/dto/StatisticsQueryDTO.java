package com.his.statistics.dto;

import lombok.Data;

/**
 * 业务数据统计查询参数
 */
@Data
public class StatisticsQueryDTO {

    /** 开始日期（YYYY-MM-DD，含） */
    private String startDate;

    /** 结束日期（YYYY-MM-DD，含） */
    private String endDate;

    /** 科室过滤（null 表示全部科室） */
    private Integer departmentId;
}
