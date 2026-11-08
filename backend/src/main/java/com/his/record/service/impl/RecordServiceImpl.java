package com.his.record.service.impl;

import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.entity.Register;
import com.his.mapper.FeeRecordMapper;
import com.his.mapper.RecordMapper;
import com.his.mapper.RegisterMapper;
import com.his.record.dto.RecordFlowDTO;
import com.his.record.dto.RecordHistoryPageVO;
import com.his.record.dto.RecordQueryDTO;
import com.his.record.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Override
    public RecordHistoryPageVO history(RecordQueryDTO query) {
        if (query.getOffset() == null) {
            query.setOffset(0);
        }
        if (query.getSize() == null) {
            query.setSize(10);
        }
        RecordHistoryPageVO page = new RecordHistoryPageVO();
        page.setList(recordMapper.selectHistory(query));
        page.setTotal(recordMapper.countHistory(query));
        return page;
    }

    @Override
    public RecordFlowDTO flow(Integer registerId, String caseNumber) {
        Integer rid = registerId;
        // 未指定具体就诊时，按病历号取最近一次就诊
        if (rid == null && caseNumber != null && !caseNumber.trim().isEmpty()) {
            Register r = registerMapper.selectLatestByCaseNumber(caseNumber.trim());
            rid = (r == null) ? null : r.getId();
        }
        if (rid == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "未找到对应的就诊记录");
        }

        RecordFlowDTO dto = recordMapper.selectRegisterBase(rid);
        if (dto == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "未找到对应的就诊记录");
        }
        dto.setMedicalRecord(recordMapper.selectMedicalRecordByRegisterId(rid));
        dto.setDiseases(recordMapper.selectDiseasesByRegisterId(rid));
        dto.setItems(recordMapper.selectFlowItems(rid));
        dto.setPrescriptions(recordMapper.selectFlowPrescriptions(rid));
        dto.setFees(feeRecordMapper.selectRecordsByRegisterId(rid));
        return dto;
    }
}
