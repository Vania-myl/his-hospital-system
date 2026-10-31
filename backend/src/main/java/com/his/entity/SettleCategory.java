package com.his.entity;

import lombok.Data;

/**
 * 结算类别表
 */
@Data
public class SettleCategory {
    private Integer id;
    private String settleCode;
    private String settleName;
    private Integer sequenceNo;
    private Integer delmark;
}
