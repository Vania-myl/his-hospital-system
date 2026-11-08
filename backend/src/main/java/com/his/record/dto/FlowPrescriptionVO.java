package com.his.record.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 就诊全流程中的处方明细
 */
@Data
public class FlowPrescriptionVO {

    private String drugName;
    private String drugFormat;
    private String drugUsage;
    private String drugNumber;
    private BigDecimal drugPrice;

    /** 状态码：1已开立/2已发药/3已退药/4已撤销 */
    private String state;

    /** 开立时间（格式化后字符串） */
    private String creationTime;
}
