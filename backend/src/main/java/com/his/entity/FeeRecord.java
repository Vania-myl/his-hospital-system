package com.his.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用流水表
 */
@Data
public class FeeRecord {
    private Integer id;
    private Integer registerId;
    private String itemType;
    private Integer itemId;
    private String feeType;
    private BigDecimal feeAmount;
    private String feeMethod;
    private Integer refundTargetId;
    private Integer operatorId;
    private String remark;
    private Date createTime;
}
