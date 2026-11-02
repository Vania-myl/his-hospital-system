package com.his.record.dto;

import com.his.entity.MedicalRecord;
import com.his.register.dto.FeeRecordVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 就诊全流程记录（一次就诊的完整业务链条）
 */
@Data
public class RecordFlowDTO {

    // ---- 挂号信息 ----
    private Integer registerId;
    private String caseNumber;
    private String realName;
    private String gender;
    private Integer age;
    private String cardNumber;
    private String deptName;
    private String registLevelName;
    private String visitDate;
    private String noon;
    private Integer visitState;
    private BigDecimal registMoney;

    /** 本次就诊收费合计 */
    private BigDecimal chargeAmount;

    // ---- 医生接诊 ----
    private MedicalRecord medicalRecord;
    /** 诊断疾病名称列表 */
    private List<String> diseases;

    // ---- 医技项目（检查/检验/处置） ----
    private List<FlowItemVO> items;

    // ---- 处方 ----
    private List<FlowPrescriptionVO> prescriptions;

    // ---- 费用流水 ----
    private List<FeeRecordVO> fees;
}
