package com.his.register.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 费用流水查询结果：患者信息 + 汇总金额 + 明细列表
 */
@Data
public class FeeQueryVO {

    private String caseNumber;
    private String realName;
    private String cardNumber;
    private String gender;
    private Integer age;

    /** 累计收费 */
    private BigDecimal chargeTotal;
    /** 累计退费 */
    private BigDecimal refundTotal;

    private List<FeeRecordVO> records;
}
