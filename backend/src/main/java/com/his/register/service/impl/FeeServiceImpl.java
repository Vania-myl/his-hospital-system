package com.his.register.service.impl;

import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.entity.FeeRecord;
import com.his.entity.Register;
import com.his.mapper.FeeRecordMapper;
import com.his.mapper.RegisterMapper;
import com.his.register.dto.FeeQueryVO;
import com.his.register.dto.FeeRecordVO;
import com.his.register.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private FeeRecordMapper feeRecordMapper;
    @Autowired
    private RegisterMapper registerMapper;

    @Override
    public FeeQueryVO list(String caseNumber) {
        Register register = requireRegister(caseNumber);
        List<FeeRecordVO> records = feeRecordMapper.selectRecordsByCaseNumber(register.getCaseNumber());

        FeeQueryVO vo = new FeeQueryVO();
        vo.setCaseNumber(register.getCaseNumber());
        vo.setRealName(register.getRealName());
        vo.setCardNumber(register.getCardNumber());
        vo.setGender(register.getGender());
        vo.setAge(register.getAge());

        BigDecimal chargeTotal = BigDecimal.ZERO;
        BigDecimal refundTotal = BigDecimal.ZERO;
        for (FeeRecordVO r : records) {
            if (r.getFeeAmount() == null) {
                continue;
            }
            if ("收费".equals(r.getFeeType())) {
                chargeTotal = chargeTotal.add(r.getFeeAmount());
            } else if ("退费".equals(r.getFeeType())) {
                refundTotal = refundTotal.add(r.getFeeAmount());
            }
        }
        vo.setChargeTotal(chargeTotal);
        vo.setRefundTotal(refundTotal);
        vo.setRecords(records);
        return vo;
    }

    @Override
    public List<FeeRecordVO> refundable(String caseNumber) {
        Register register = requireRegister(caseNumber);
        return feeRecordMapper.selectRefundableByCaseNumber(register.getCaseNumber());
    }

    @Override
    @Transactional
    public void refund(Integer feeRecordId) {
        FeeRecord fee = feeRecordMapper.selectById(feeRecordId);
        if (fee == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "收费记录不存在");
        }
        if (!"收费".equals(fee.getFeeType())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该记录不是收费记录，不可退费");
        }
        if ("挂号费".equals(fee.getItemType())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "挂号费请通过退号办理");
        }
        if (feeRecordMapper.countByRefundTargetId(feeRecordId) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该收费记录已退费");
        }

        // 已执行 / 已发药的项目不可退费（状态码 1 = 已开立，未消费）
        String state = feeRecordMapper.selectItemState(fee.getItemType(), fee.getItemId());
        if (state == null || !"1".equals(state)) {
            throw new BusinessException(ResultCode.CANNOT_REFUND, "项目已执行 / 已发药，不可退费");
        }

        FeeRecord refund = new FeeRecord();
        refund.setRegisterId(fee.getRegisterId());
        refund.setItemType(fee.getItemType());
        refund.setItemId(fee.getItemId());
        refund.setFeeType("退费");
        refund.setFeeAmount(fee.getFeeAmount());
        refund.setFeeMethod(fee.getFeeMethod());
        refund.setRefundTargetId(fee.getId());
        refund.setOperatorId(currentUserId());
        refund.setRemark("退费");
        refund.setCreateTime(new Date());
        feeRecordMapper.insert(refund);
    }

    private Register requireRegister(String caseNumber) {
        if (caseNumber == null || caseNumber.trim().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "病历号不能为空");
        }
        Register register = registerMapper.selectLatestByCaseNumber(caseNumber.trim());
        if (register == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "病历号不存在：" + caseNumber);
        }
        return register;
    }

    private Integer currentUserId() {
        LoginUser user = UserContext.get();
        return user == null ? null : user.getId();
    }
}
