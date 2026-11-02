package com.his.statistics.dto;

import lombok.Data;

/**
 * 科室就诊人次分布（柱状图）
 */
@Data
public class DeptCountVO {

    /** 科室名称 */
    private String deptName;

    /** 就诊人次 */
    private Integer count;
}
