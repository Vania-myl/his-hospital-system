package com.his.register.service.impl;

import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.entity.FeeRecord;
import com.his.entity.Register;
import com.his.mapper.ChargeMapper;
import com.his.mapper.FeeRecordMapper;
import com.his.mapper.RegisterMapper;
import com.his.register.dto.ChargeDTO;
import com.his.register.dto.ChargeItemDTO;
import com.his.register.dto.ChargeItemVO;
import com.his.register.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private com.his.mapper.BusinessLockMapper businessLocks;
    @Autowired
    private ChargeMapper chargeMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FeeRecordMapper feeRecordMapper;

    @Override
    public List<ChargeItemVO> pending(String caseNumber) {
        com.his.common.util.RoleAccess.role("window");
        Register register = requireRegister(caseNumber);
        List<ChargeItemVO> items = loadPending(register.getId());
        for (ChargeItemVO vo : items) {
            vo.setAmount(calcAmount(vo));
        }
        return items;
    }

    @Override
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public List<FeeRecord> charge(ChargeDTO dto) {
        com.his.common.util.RoleAccess.role("window");
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要收费的项目");
        }
        Register register = requireRegister(dto.getCaseNumber());

        if (!"window".equals(UserContext.get().getRole())) throw new BusinessException(403, "仅收费窗口可以收费");
        java.util.Set<String> unique = new java.util.HashSet<>();
        java.util.List<ChargeItemDTO> sorted = new java.util.ArrayList<>(dto.getItems());
        for (ChargeItemDTO item : sorted) {
            if (item == null || item.getItemId() == null || item.getItemType() == null)
                throw new BusinessException("收费项目参数不完整");
            if (!unique.add(item.getItemType() + ":" + item.getItemId()))
                throw new BusinessException("同一次请求不能重复选择相同项目");
        }
        sorted.sort(java.util.Comparator.comparing(ChargeItemDTO::getItemType).thenComparing(ChargeItemDTO::getItemId));
        for (ChargeItemDTO item : sorted) businessLocks.item(item.getItemType(), item.getItemId());

        // 以「类型:id」为键，建立待收费项目索引（防止重复收费）
        Map<String, ChargeItemVO> pendingMap = new HashMap<>();
        for (ChargeItemVO vo : loadPending(register.getId())) {
            vo.setAmount(calcAmount(vo));
            pendingMap.put(vo.getItemType() + ":" + vo.getItemId(), vo);
        }

        String feeMethod = dto.getFeeMethod() == null || dto.getFeeMethod().trim().isEmpty()
                ? "现金" : dto.getFeeMethod().trim();

        List<FeeRecord> records = new ArrayList<>();
        for (ChargeItemDTO item : dto.getItems()) {
            ChargeItemVO vo = pendingMap.get(item.getItemType() + ":" + item.getItemId());
            if (vo == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST,
                        "项目不存在或已收费：" + item.getItemType() + "(" + item.getItemId() + ")");
            }
            FeeRecord fee = new FeeRecord();
            fee.setRegisterId(register.getId());
            fee.setItemType(vo.getItemType());
            fee.setItemId(vo.getItemId());
            fee.setFeeType("收费");
            fee.setFeeAmount(vo.getAmount());
            fee.setFeeMethod(feeMethod);
            fee.setOperatorId(currentUserId());
            fee.setCreateTime(new Date());
            feeRecordMapper.insert(fee);
            records.add(fee);
        }
        return records;
    }

    private List<ChargeItemVO> loadPending(Integer registerId) {
        List<ChargeItemVO> items = new ArrayList<>();
        items.addAll(chargeMapper.selectPendingPrescription(registerId));
        items.addAll(chargeMapper.selectPendingCheck(registerId));
        items.addAll(chargeMapper.selectPendingInspection(registerId));
        items.addAll(chargeMapper.selectPendingDisposal(registerId));
        return items;
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

    private BigDecimal calcAmount(ChargeItemVO vo) {
        BigDecimal price = vo.getUnitPrice() == null ? BigDecimal.ZERO : vo.getUnitPrice();
        int qty = 1;
        if (vo.getQuantity() != null && !vo.getQuantity().trim().isEmpty()) {
            try {
                qty = Integer.parseInt(vo.getQuantity().trim());
            } catch (Exception ignored) {
                qty = 1;
            }
        }
        return price.multiply(BigDecimal.valueOf(qty));
    }

    private Integer currentUserId() {
        LoginUser user = UserContext.get();
        return user == null ? null : user.getId();
    }
}
