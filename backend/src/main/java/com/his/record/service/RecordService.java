package com.his.record.service;

import com.his.record.dto.RecordFlowDTO;
import com.his.record.dto.RecordHistoryPageVO;
import com.his.record.dto.RecordQueryDTO;

public interface RecordService {

    /** 历史业务查询（分页） */
    RecordHistoryPageVO history(RecordQueryDTO query);

    /** 就诊全流程记录（registerId 优先，否则按病历号取最近一次就诊） */
    RecordFlowDTO flow(Integer registerId, String caseNumber);
}
