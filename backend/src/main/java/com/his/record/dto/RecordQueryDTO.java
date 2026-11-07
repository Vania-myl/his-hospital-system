package com.his.record.dto;

import lombok.Data;

/**
 * 历史业务查询参数
 */
@Data
public class RecordQueryDTO {

    /** 病历号 */
    private String caseNumber;

    /** 患者姓名（模糊） */
    private String realName;

    /** 就诊开始日期（YYYY-MM-DD，含） */
    private String startDate;

    /** 就诊结束日期（YYYY-MM-DD，含） */
    private String endDate;

    /** 分页偏移 */
    private Integer offset;

    /** 每页条数 */
    private Integer size;
}
