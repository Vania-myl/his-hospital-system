package com.his.register.service;

import com.his.entity.FeeRecord;
import com.his.register.dto.ChargeDTO;
import com.his.register.dto.ChargeItemVO;

import java.util.List;

public interface ChargeService {

    /** 查询某患者当前待收费的项目（处方/检查/检验/处置） */
    List<ChargeItemVO> pending(String caseNumber);

    /** 收费：对选中项目生成收费流水 */
    List<FeeRecord> charge(ChargeDTO dto);
}
