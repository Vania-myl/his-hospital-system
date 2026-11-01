package com.his.mapper;

import com.his.entity.FeeRecord;
import com.his.register.dto.FeeRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeeRecordMapper {

    int insert(FeeRecord feeRecord);

    FeeRecord selectById(Integer id);

    List<FeeRecord> selectByRegisterId(Integer registerId);

    /** 某患者全部费用流水（含项目名称/单价/数量等明细） */
    List<FeeRecordVO> selectRecordsByCaseNumber(String caseNumber);

    /** 某患者可退费的已收费项目（排除挂号费、已退费记录） */
    List<FeeRecordVO> selectRefundableByCaseNumber(String caseNumber);

    /** 查询某项目当前状态码（处方/检查/检验/处置） */
    String selectItemState(@Param("itemType") String itemType, @Param("itemId") Integer itemId);

    /** 某收费流水已产生的退费条数 */
    int countByRefundTargetId(Integer refundTargetId);
}
