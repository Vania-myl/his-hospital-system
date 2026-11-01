package com.his.register.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 待收费项目视图
 */
@Data
public class ChargeItemVO {

    /** 项目类型 处方/检查/检验/处置 */
    private String itemType;
    /** 对应业务表记录 id */
    private Integer itemId;
    /** 项目名称 */
    private String itemName;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 数量（处方用，字符串；其他为 null） */
    private String quantity;
    /** 应付金额 = 单价 × 数量（由 Service 计算） */
    private BigDecimal amount;
}
