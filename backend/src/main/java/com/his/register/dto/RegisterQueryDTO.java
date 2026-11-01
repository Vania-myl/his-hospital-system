package com.his.register.dto;

import lombok.Data;

/**
 * 挂号查询请求
 */
@Data
public class RegisterQueryDTO {

    private String caseNumber;
    private String realName;
    private Integer visitState;
    /** yyyy-MM-dd，可选 */
    private String visitDate;
    private Integer departmentId;

    private Integer page;
    private Integer size;
    /** 由 Service 计算得出 */
    private Integer offset;
}
