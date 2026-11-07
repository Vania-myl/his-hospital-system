package com.his.record.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 历史业务查询列表项（一次就诊一行）
 */
@Data
public class RecordHistoryVO {

    /** 挂号记录 id（跳转全流程用） */
    private Integer registerId;

    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;

    private String deptName;
    private String registLevelName;

    /** 就诊时间（格式化后字符串） */
    private String visitDate;

    /** 午别 */
    private String noon;

    /** 就诊状态：1已挂号/2医生接诊/3看诊结束/4已退号 */
    private Integer visitState;

    /** 本次就诊收费合计 */
    private BigDecimal chargeAmount;
}
