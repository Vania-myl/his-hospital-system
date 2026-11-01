package com.his.register.service;

import com.his.register.dto.FeeQueryVO;
import com.his.register.dto.FeeRecordVO;

import java.util.List;

public interface FeeService {

    /** 费用流水查询：患者信息 + 收费/退费汇总 + 明细 */
    FeeQueryVO list(String caseNumber);

    /** 退费列表：某患者可退费的已收费项目 */
    List<FeeRecordVO> refundable(String caseNumber);

    /** 退费：校验项目未消费后生成退费流水 */
    void refund(Integer feeRecordId);
}
