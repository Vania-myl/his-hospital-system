package com.his.register.service.impl;

import com.his.common.LoginUser;
import com.his.common.ResultCode;
import com.his.common.exception.BusinessException;
import com.his.common.util.UserContext;
import com.his.entity.FeeRecord;
import com.his.entity.Register;
import com.his.entity.RegistLevel;
import com.his.mapper.FeeRecordMapper;
import com.his.mapper.RegisterMapper;
import com.his.mapper.RegistLevelMapper;
import com.his.register.dto.RegisterDTO;
import com.his.register.dto.RegisterQueryDTO;
import com.his.register.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    /** 病历本费用（业务规则，可按需调整） */
    private static final BigDecimal BOOK_FEE = new BigDecimal("1.00");

    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FeeRecordMapper feeRecordMapper;
    @Autowired
    private RegistLevelMapper registLevelMapper;

    @Override
    @Transactional
    public Register register(RegisterDTO dto) {
        if (dto.getRealName() == null || dto.getRealName().trim().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "患者姓名不能为空");
        }
        if (dto.getDepartmentId() == null || dto.getEmployeeId() == null
                || dto.getRegistLevelId() == null || dto.getSettleCategoryId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "挂号科室、医生、号别、结算类别不能为空");
        }

        // 病历号：新患者自动生成；老患者按身份证号复用
        String caseNumber = dto.getCaseNumber();
        if (caseNumber == null || caseNumber.trim().isEmpty()) {
            if (dto.getCardNumber() != null && !dto.getCardNumber().trim().isEmpty()) {
                Register existed = registerMapper.selectLatestByCardNumber(dto.getCardNumber().trim());
                caseNumber = existed == null ? generateCaseNumber() : existed.getCaseNumber();
            } else {
                caseNumber = generateCaseNumber();
            }
        }

        // 挂号费 = 号别挂号费（+ 病历本费）
        RegistLevel level = registLevelMapper.selectById(dto.getRegistLevelId());
        if (level == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "挂号级别不存在");
        }
        BigDecimal registMoney = level.getRegistFee() == null ? BigDecimal.ZERO : level.getRegistFee();
        if ("是".equals(dto.getIsBook())) {
            registMoney = registMoney.add(BOOK_FEE);
        }

        Register register = new Register();
        register.setCaseNumber(caseNumber);
        register.setRealName(dto.getRealName().trim());
        register.setGender(dto.getGender());
        register.setCardNumber(dto.getCardNumber());
        register.setBirthdate(parseDate(dto.getBirthdate()));
        register.setAge(dto.getAge());
        register.setAgeType(dto.getAgeType());
        register.setHomeAddress(dto.getHomeAddress());
        register.setVisitDate(parseDateTime(dto.getVisitDate()));
        register.setNoon(dto.getNoon() == null || dto.getNoon().trim().isEmpty() ? "上午" : dto.getNoon());
        register.setDepartmentId(dto.getDepartmentId());
        register.setEmployeeId(dto.getEmployeeId());
        register.setRegistLevelId(dto.getRegistLevelId());
        register.setSettleCategoryId(dto.getSettleCategoryId());
        register.setIsBook(dto.getIsBook() == null || dto.getIsBook().isEmpty() ? "否" : dto.getIsBook());
        register.setRegistMethod(dto.getRegistMethod() == null || dto.getRegistMethod().isEmpty() ? "现金" : dto.getRegistMethod());
        register.setRegistMoney(registMoney);
        register.setVisitState(1);
        registerMapper.insert(register);

        // 挂号费流水
        FeeRecord fee = new FeeRecord();
        fee.setRegisterId(register.getId());
        fee.setItemType("挂号费");
        fee.setItemId(register.getId());
        fee.setFeeType("收费");
        fee.setFeeAmount(registMoney);
        fee.setFeeMethod(register.getRegistMethod());
        fee.setOperatorId(currentUserId());
        fee.setCreateTime(new Date());
        feeRecordMapper.insert(fee);

        return register;
    }

    @Override
    public List<Register> list(RegisterQueryDTO query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
        query.setOffset((query.getPage() - 1) * query.getSize());
        return registerMapper.selectList(query);
    }

    @Override
    public int count(RegisterQueryDTO query) {
        return registerMapper.countList(query);
    }

    @Override
    @Transactional
    public void refund(Integer registerId) {
        Register register = registerMapper.selectById(registerId);
        if (register == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "挂号记录不存在");
        }
        if (register.getVisitState() != null && register.getVisitState() == 4) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该挂号已退号");
        }
        if (register.getVisitState() == null || register.getVisitState() != 1) {
            throw new BusinessException(ResultCode.CANNOT_CANCEL, "患者已就诊，不可退号");
        }

        registerMapper.updateVisitState(registerId, 4);

        // 针对原挂号费收费流水，产生退费
        List<FeeRecord> fees = feeRecordMapper.selectByRegisterId(registerId);
        for (FeeRecord fee : fees) {
            if ("挂号费".equals(fee.getItemType()) && "收费".equals(fee.getFeeType())) {
                FeeRecord refund = new FeeRecord();
                refund.setRegisterId(registerId);
                refund.setItemType("挂号费");
                refund.setItemId(registerId);
                refund.setFeeType("退费");
                refund.setFeeAmount(fee.getFeeAmount());
                refund.setFeeMethod(fee.getFeeMethod());
                refund.setRefundTargetId(fee.getId());
                refund.setOperatorId(currentUserId());
                refund.setRemark("退号");
                refund.setCreateTime(new Date());
                feeRecordMapper.insert(refund);
            }
        }
    }

    private Integer currentUserId() {
        LoginUser user = UserContext.get();
        return user == null ? null : user.getId();
    }

    private String generateCaseNumber() {
        return "BN" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private Date parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) {
            return new Date();
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.trim());
        } catch (Exception e) {
            return new Date();
        }
    }

    private Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
