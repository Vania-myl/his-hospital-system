package com.his.register.dto;

import lombok.Data;

import java.util.List;

/**
 * 收费请求体
 */
@Data
public class ChargeDTO {

    private String caseNumber;
    /** 收费方式 现金/银行卡/医保/微信/支付宝 */
    private String feeMethod;
    private List<ChargeItemDTO> items;
}
