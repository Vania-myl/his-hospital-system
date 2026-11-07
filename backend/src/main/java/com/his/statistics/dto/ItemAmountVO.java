package com.his.statistics.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收费项目金额分布（饼图）
 */
@Data
public class ItemAmountVO {

    /** 项目类型：挂号费/检查/检验/处置/处方 */
    private String itemType;

    /** 收费金额 */
    private BigDecimal amount;
}
