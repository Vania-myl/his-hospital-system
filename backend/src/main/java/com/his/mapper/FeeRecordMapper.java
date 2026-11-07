package com.his.mapper;

import com.his.entity.FeeRecord;
import com.his.register.dto.FeeRecordVO;
import com.his.statistics.dto.ItemAmountVO;
import com.his.statistics.dto.StatisticsQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FeeRecordMapper {

    int insert(FeeRecord feeRecord);

    FeeRecord selectById(Integer id);

    List<FeeRecord> selectByRegisterId(Integer registerId);

    /** 某患者全部费用流水（含项目名称/单价/数量等明细） */
    List<FeeRecordVO> selectRecordsByCaseNumber(String caseNumber);

    /** 某次就诊（register_id）的费用流水明细 */
    List<FeeRecordVO> selectRecordsByRegisterId(Integer registerId);

    /** 某患者可退费的已收费项目（排除挂号费、已退费记录） */
    List<FeeRecordVO> selectRefundableByCaseNumber(String caseNumber);

    /** 查询某项目当前状态码（处方/检查/检验/处置） */
    String selectItemState(@Param("itemType") String itemType, @Param("itemId") Integer itemId);

    /** 某收费流水已产生的退费条数 */
    int countByRefundTargetId(Integer refundTargetId);

    /** 统计：收费金额合计 */
    BigDecimal sumChargeAmount(StatisticsQueryDTO query);

    /** 统计：退费金额合计 */
    BigDecimal sumRefundAmount(StatisticsQueryDTO query);

    /** 统计：各收费项目金额分布 */
    List<ItemAmountVO> groupByItemType(StatisticsQueryDTO query);
}
