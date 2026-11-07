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
    private com.his.mapper.BusinessLockMapper businessLocks;
    @Autowired
    private FeeRecordMapper feeRecordMapper;
    @Autowired
    private RegisterMapper registerMapper;

    @Override
    public FeeQueryVO list(String caseNumber) {
        LoginUser viewer = com.his.common.util.RoleAccess.role("window", "doctor", "pharmacy");
        Register register = requireRegister(caseNumber);
        List<FeeRecordVO> records = feeRecordMapper.selectRecordsByCaseNumber(register.getCaseNumber());

        if ("doctor".equals(viewer.getRole())) {
            boolean allowed = records.stream().anyMatch(row -> {
                Register visit = registerMapper.selectById(row.getRegisterId());
                return visit != null && viewer.getId().equals(visit.getEmployeeId());
            });
            if (!allowed) throw new BusinessException(403, "只能查询本人接诊患者费用");
            records.removeIf(row -> {
                Register visit = registerMapper.selectById(row.getRegisterId());
                return visit == null || !viewer.getId().equals(visit.getEmployeeId());
            });
        } else if ("pharmacy".equals(viewer.getRole())) {
            records.removeIf(row -> !"处方".equals(row.getItemType()));
            for (FeeRecordVO row : records) row.setCardNumber(null);
        }
        FeeQueryVO vo = new FeeQueryVO();
        vo.setCaseNumber(register.getCaseNumber());
        vo.setRealName(register.getRealName());
        vo.setCardNumber("pharmacy".equals(viewer.getRole()) ? null : register.getCardNumber());
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
        com.his.common.util.RoleAccess.role("window");
        Register register = requireRegister(caseNumber);
        return feeRecordMapper.selectRefundableByCaseNumber(register.getCaseNumber());
    }

    @Override
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public void refund(Integer feeRecordId) {
        com.his.common.util.RoleAccess.role("window");
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
        if (!"window".equals(UserContext.get().getRole())) throw new BusinessException(403, "仅收费窗口可以退费");
        businessLocks.item(fee.getItemType(), fee.getItemId());
        if (feeRecordMapper.countByRefundTargetId(feeRecordId) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该收费记录已退费");
        }

        // 已执行 / 已发药的项目不可退费（状态码 1 = 已开立，未消费）
        String state = feeRecordMapper.selectItemState(fee.getItemType(), fee.getItemId());
        if (!("1".equals(state) || ("处方".equals(fee.getItemType()) && "3".equals(state)))) {
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
