package com.his.mapper;

import com.his.entity.MedicalRecord;
import com.his.record.dto.FlowItemVO;
import com.his.record.dto.FlowPrescriptionVO;
import com.his.record.dto.RecordFlowDTO;
import com.his.record.dto.RecordHistoryVO;
import com.his.record.dto.RecordQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 综合管理：历史业务查询 / 就诊全流程记录（只读聚合查询）
 */
public interface RecordMapper {

    List<RecordHistoryVO> selectHistory(RecordQueryDTO query);

    int countHistory(RecordQueryDTO query);

    /** 挂号基本信息 + 科室名 + 号别名 + 收费合计 */
    RecordFlowDTO selectRegisterBase(@Param("registerId") Integer registerId);

    MedicalRecord selectMedicalRecordByRegisterId(@Param("registerId") Integer registerId);

    List<String> selectDiseasesByRegisterId(@Param("registerId") Integer registerId);

    /** 检查/检验/处置 汇总 */
    List<FlowItemVO> selectFlowItems(@Param("registerId") Integer registerId);

    List<FlowPrescriptionVO> selectFlowPrescriptions(@Param("registerId") Integer registerId);
}
