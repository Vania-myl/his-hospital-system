package com.his.pharmacy.service.impl;

import com.his.common.exception.BusinessException;
import com.his.common.util.RoleAccess;
import com.his.entity.*;
import com.his.mapper.*;
import com.his.outpatient.dto.*;
import com.his.pharmacy.service.PharmacyService;
import com.his.register.service.VisitLifecycleService;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
public class PharmacyServiceImpl implements PharmacyService {
  private final PharmacyMapper mapper;
  private final OutpatientMapper records;
  private final RegisterMapper registers;
  private final VisitLifecycleService lifecycle;

  @Override
  public PageResult<Map<String, Object>> prescriptions(PageQuery q) {
    String role = RoleAccess.role("doctor", "pharmacy").getRole();
    if ("doctor".equals(role)) {
      if (q.getRegisterId() == null) throw new BusinessException("请选择本次挂号");
      Register r = registers.selectById(q.getRegisterId());
      if (r == null || !RoleAccess.role("doctor").getId().equals(r.getEmployeeId()))
        throw new BusinessException(403, "只能查询本人接诊患者处方");
    }
    RoleAccess.date(q.getDate());
    return new PageResult<>(mapper.prescriptions(q), mapper.prescriptionCount(q), q);
  }

  private int quantity(String value) {
    if (value == null || !value.matches("[1-9][0-9]{0,5}"))
      throw new BusinessException("药品数量必须为1至999999的整数");
    return Integer.parseInt(value);
  }

  private Register editing(Integer id) {
    Register r = lifecycle.owned(id);
    if (!Integer.valueOf(2).equals(r.getVisitState()))
      throw new BusinessException("只有接诊中的患者可以开立或撤销处方");
    return r;
  }

  private DrugInfo drug(Integer id) {
    DrugInfo d = mapper.drug(id);
    if (d == null) throw new BusinessException("药品不存在");
    return d;
  }

  private Prescription prescription(Integer id) {
    Prescription p = mapper.prescription(id);
    if (p == null) throw new BusinessException("处方不存在");
    return p;
  }

  @Override
  public Prescription prescribe(Prescription input) {
    RoleAccess.role("doctor");
    if (input == null || input.getRegisterId() == null || input.getDrugId() == null)
      throw new BusinessException("请选择患者与药品");
    editing(input.getRegisterId());
    MedicalRecord record = records.record(input.getRegisterId());
    if (record == null || !Integer.valueOf(1).equals(record.getDiagnosisState()))
      throw new BusinessException("请先完成病历确诊");
    int qty = quantity(input.getDrugNumber());
    String usage = RoleAccess.text(input.getDrugUsage(), "用法用量频次", 255, true);
    DrugInfo d = drug(input.getDrugId());
    if (!Integer.valueOf(1).equals(d.getDelmark())) throw new BusinessException("该药品已停用");
    if (d.getDrugPrice().multiply(BigDecimal.valueOf(qty)).compareTo(new BigDecimal("999999.99"))
        > 0) throw new BusinessException("处方金额超过系统范围");
    Prescription p = new Prescription();
    p.setRegisterId(input.getRegisterId());
    p.setDrugId(d.getId());
    p.setDrugNumber(String.valueOf(qty));
    p.setDrugUsage(usage);
    p.setUnitPrice(d.getDrugPrice());
    p.setDrugState("1");
    mapper.insertPrescription(p);
    return p;
  }

  @Override
  public void cancel(Integer id) {
    RoleAccess.role("doctor");
    // 与开方一致：先锁挂号，再锁处方。预读仅用于定位挂号，锁后再次核验。
    Integer registerId = mapper.prescriptionVisit(id);
    if (registerId == null) throw new BusinessException("处方不存在");
    editing(registerId);
    Prescription p = prescription(id);
    if (!"1".equals(p.getDrugState())) throw new BusinessException("仅未发药处方可以撤销");
    if (!mapper.effectiveCharges(p.getRegisterId(), "处方", id).isEmpty())
      throw new BusinessException("已收费处方须先到窗口退费");
    if (mapper.state(id, "1", "4") != 1) throw new BusinessException("处方状态已变化");
  }

}
