package com.his.entity;

import lombok.Data;

/**
 * 排班表
 */
@Data
public class Scheduling {
    private Integer id;
    private String ruleName;
    private String weekRule;
    private Integer delmark;
}
