package com.his.record.dto;

import lombok.Data;

/**
 * 就诊全流程中的医技项目（检查/检验/处置）
 */
@Data
public class FlowItemVO {

    /** 项目类型：检查/检验/处置 */
    private String itemType;

    /** 医技项目名称 */
    private String techName;

    /** 状态码：1已开立/2已执行/3已完成/4已撤销 */
    private String state;

    /** 开立时间（格式化后字符串） */
    private String creationTime;

    /** 检查/检验/处置结果 */
    private String result;
}
