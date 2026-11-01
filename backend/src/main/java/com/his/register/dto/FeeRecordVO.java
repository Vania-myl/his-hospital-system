package com.his.register.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用流水明细视图（费用流水查询 / 退费列表共用）
 */
@Data
public class FeeRecordVO {

    private Integer id;
    private Integer registerId;

    /** 患者信息（取自 register） */
    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;
    private String cardNumber;

    /** 项目信息 */
    private String itemType;
    private Integer itemId;
    private String itemName;
    private String itemFormat;
    private BigDecimal unitPrice;
    private String quantity;

    /** 流水信息 */
    private String feeType;
    private BigDecimal feeAmount;
    private String feeMethod;
    private Integer refundTargetId;
    private String remark;
    private Date createTime;

    /** 项目当前状态码（处方 drug_state / 检查 check_state / 检验 inspection_state / 处置 disposal_state） */
    private String itemState;

    /** 开立科室（医技项目，取自 medical_technology.department_id） */
    private String deptName;
}
