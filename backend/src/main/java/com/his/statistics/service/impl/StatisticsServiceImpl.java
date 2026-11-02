package com.his.statistics.service.impl;

import com.his.mapper.CheckRequestMapper;
import com.his.mapper.DisposalRequestMapper;
import com.his.mapper.FeeRecordMapper;
import com.his.mapper.InspectionRequestMapper;
import com.his.mapper.PrescriptionMapper;
import com.his.mapper.RegisterMapper;
import com.his.statistics.dto.StatisticsOverviewDTO;
import com.his.statistics.dto.StatisticsQueryDTO;
import com.his.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FeeRecordMapper feeRecordMapper;
    @Autowired
    private CheckRequestMapper checkRequestMapper;
    @Autowired
    private InspectionRequestMapper inspectionRequestMapper;
    @Autowired
    private DisposalRequestMapper disposalRequestMapper;
    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    public StatisticsOverviewDTO overview(StatisticsQueryDTO query) {
        StatisticsOverviewDTO dto = new StatisticsOverviewDTO();

        // 挂号 / 退号 / 门诊人次
        int registerCount = registerMapper.countRegister(query);
        int refundCount = registerMapper.countRegisterRefund(query);
        dto.setRegisterCount(registerCount);
        dto.setRefundCount(refundCount);
        dto.setOutPatientCount(registerCount - refundCount);

        // 收费 / 退费金额
        dto.setChargeAmount(feeRecordMapper.sumChargeAmount(query));
        dto.setRefundAmount(feeRecordMapper.sumRefundAmount(query));

        // 检查 / 检验 / 处置 / 处方 / 发药
        dto.setCheckCount(checkRequestMapper.countAll(query));
        dto.setInspectionCount(inspectionRequestMapper.countAll(query));
        dto.setDisposalCount(disposalRequestMapper.countAll(query));
        dto.setPrescriptionCount(prescriptionMapper.countAll(query));
        dto.setDispenseCount(prescriptionMapper.countDispensed(query));

        // 分布（柱状图 / 饼图）
        dto.setDeptDistribution(registerMapper.countGroupByDept(query));
        dto.setItemFeeDistribution(feeRecordMapper.groupByItemType(query));

        return dto;
    }
}
