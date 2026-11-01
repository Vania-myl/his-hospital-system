package com.his.register.dto;

import lombok.Data;

/**
 * 收费请求中的单个项目
 */
@Data
public class ChargeItemDTO {

    /** 项目类型 处方/检查/检验/处置 */
    private String itemType;
    private Integer itemId;
}
