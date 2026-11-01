package com.his.register.dto;

import lombok.Data;

/**
 * 挂号请求体
 */
@Data
public class RegisterDTO {

    /** 病历号：为空则视为新患者，自动生成 */
    private String caseNumber;

    private String realName;
    private String gender;
    private String cardNumber;

    /** 出生日期 yyyy-MM-dd */
    private String birthdate;
    private Integer age;
    private String ageType;
    private String homeAddress;

    /** 看诊日期 yyyy-MM-dd HH:mm:ss，为空默认当前时间 */
    private String visitDate;
    /** 午别 上午/下午 */
    private String noon;

    private Integer departmentId;
    private Integer employeeId;
    private Integer registLevelId;
    private Integer settleCategoryId;

    /** 病历本要否 是/否 */
    private String isBook;
    /** 收费方式 现金/银行卡/医保卡/微信/支付宝 */
    private String registMethod;
}
