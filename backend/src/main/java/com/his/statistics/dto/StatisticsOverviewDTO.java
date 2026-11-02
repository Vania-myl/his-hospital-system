package com.his.statistics.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 业务数据统计总览（指标卡 + 柱状图 + 饼图）
 */
@Data
public class StatisticsOverviewDTO {

    /** 门诊人次（净人次 = 挂号量 - 退号量） */
    private Integer outPatientCount;

    /** 挂号量 */
    private Integer registerCount;

    /** 退号量 */
    private Integer refundCount;

    /** 收费金额 */
    private BigDecimal chargeAmount;

    /** 退费金额 */
    private BigDecimal refundAmount;

    /** 检查例数 */
    private Integer checkCount;

    /** 检验例数 */
    private Integer inspectionCount;

    /** 处置例数 */
    private Integer disposalCount;

    /** 处方量（开立） */
    private Integer prescriptionCount;

    /** 发药量（已发药） */
    private Integer dispenseCount;

    /** 各科室就诊人次分布 */
    private List<DeptCountVO> deptDistribution;

    /** 各收费项目金额分布 */
    private List<ItemAmountVO> itemFeeDistribution;
}
