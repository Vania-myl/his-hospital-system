package com.his.pharmacy.service.impl;

import com.his.common.exception.BusinessException;
import com.his.common.util.RoleAccess;
import com.his.entity.*;
import com.his.mapper.*;
import com.his.outpatient.dto.*;
import com.his.pharmacy.dto.StockDTO;
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

  private void changeStock(DrugInfo d, int delta, Integer p, String type, String reason) {
    if (mapper.stock(d.getId(), delta) != 1) throw new BusinessException("库存不足或超出1000000的上限");
    mapper.transaction(
        d.getId(),
        p,
        type,
        delta,
        d.getStockQuantity() + delta,
        reason,
        RoleAccess.role("pharmacy").getId());
  }

  @Override
  public void dispense(Integer id) {
    RoleAccess.role("pharmacy");
    Prescription p = prescription(id);
    if (!"1".equals(p.getDrugState())) throw new BusinessException("该处方已发药、退药或撤销，请刷新列表");
    Register r = registers.selectById(p.getRegisterId());
    if (r == null || Integer.valueOf(4).equals(r.getVisitState()))
      throw new BusinessException("挂号无效或已退号，不能发药");
    List<FeeRecord> paid = mapper.effectiveCharges(p.getRegisterId(), "处方", id);
    DrugInfo d = drug(p.getDrugId());
    if (!Integer.valueOf(1).equals(d.getDelmark())) throw new BusinessException("药品已停用，请先核对处方");
    int qty = quantity(p.getDrugNumber());
    BigDecimal amount =
        (p.getUnitPrice() == null ? d.getDrugPrice() : p.getUnitPrice())
            .multiply(BigDecimal.valueOf(qty));
    if (paid.size() != 1 || paid.get(0).getFeeAmount().compareTo(amount) < 0)
      throw new BusinessException("未缴费、已退费或收费异常，不能发药");
    changeStock(d, -qty, id, "发药", "已核对处方并发药");
    if (mapper.state(id, "1", "2") != 1) throw new BusinessException("处方状态已变化");
  }

  @Override
  public void returnDrug(Integer id, String reason) {
    RoleAccess.role("pharmacy");
    reason = RoleAccess.text(reason, "退药原因", 255, true);
    Prescription p = prescription(id);
    if (!"2".equals(p.getDrugState())) throw new BusinessException("只有已发药且未退药的处方允许退药");
    DrugInfo d = drug(p.getDrugId());
    changeStock(d, quantity(p.getDrugNumber()), id, "退药", reason);
    if (mapper.state(id, "2", "3") != 1) throw new BusinessException("处方状态已变化");
  }

  @Override
  public PageResult<DrugInfo> drugs(PageQuery q) {
    RoleAccess.role("pharmacy");
    return new PageResult<>(mapper.drugs(q), mapper.drugCount(q), q);
  }

  @Override
  public DrugInfo saveDrug(Integer id, DrugInfo input) {
    RoleAccess.role("pharmacy");
    if (input == null) throw new BusinessException("药品信息不能为空");
    input.setDrugCode(RoleAccess.text(input.getDrugCode(), "药品编码", 255, true));
    input.setDrugName(RoleAccess.text(input.getDrugName(), "药品名称", 255, true));
    input.setDrugFormat(RoleAccess.text(input.getDrugFormat(), "规格", 255, false));
    input.setDrugUnit(RoleAccess.text(input.getDrugUnit(), "包装单位", 16, true));
    input.setManufacturer(RoleAccess.text(input.getManufacturer(), "生产厂家", 255, false));
    input.setDrugDosage(RoleAccess.text(input.getDrugDosage(), "剂型", 64, false));
    input.setDrugType(RoleAccess.text(input.getDrugType(), "类型", 64, false));
    input.setMnemonicCode(RoleAccess.text(input.getMnemonicCode(), "助记码", 255, false));
    if (input.getDrugPrice() == null
        || input.getDrugPrice().signum() < 0
        || input.getDrugPrice().compareTo(new BigDecimal("999999.99")) > 0
        || input.getDrugPrice().stripTrailingZeros().scale() > 2)
      throw new BusinessException("药品价格须为最多两位小数的非负金额");
    if (id != null) {
      DrugInfo old = drug(id);
      if (old.getDrugPrice().compareTo(input.getDrugPrice()) != 0 && mapper.pendingCount(id) > 0)
        throw new BusinessException("存在待发药处方，暂不能调整价格");
    }
    if (mapper.codeCount(input.getDrugCode(), id) > 0) throw new BusinessException("药品编码已存在");
    input.setId(id);
    try {
      if (id == null) mapper.insertDrug(input);
      else mapper.updateDrug(input);
    } catch (org.springframework.dao.DuplicateKeyException e) {
      throw new BusinessException("药品编码已存在");
    }
    return mapper.drug(input.getId());
  }

  @Override
  public void disable(Integer id) {
    RoleAccess.role("pharmacy");
    drug(id);
    if (mapper.pendingCount(id) > 0) throw new BusinessException("存在待发药处方，请先完成或撤销后停用");
    if (mapper.disable(id) != 1) throw new BusinessException("该药品已经停用");
  }

  @Override
  public void stock(Integer id, StockDTO dto) {
    RoleAccess.role("pharmacy");
    if (dto == null || dto.getQuantity() == null || dto.getType() == null)
      throw new BusinessException("请填写库存操作信息");
    String reason = RoleAccess.text(dto.getReason(), "操作原因", 255, true);
    if (dto.getQuantity() < 0 || dto.getQuantity() > 1000000)
      throw new BusinessException("库存数量须为0至1000000的整数");
    DrugInfo d = drug(id);
    int delta;
    switch (dto.getType()) {
      case "入库":
        delta = dto.getQuantity();
        break;
      case "报损":
        delta = -dto.getQuantity();
        break;
      case "盘点":
        delta = dto.getQuantity() - d.getStockQuantity();
        break;
      default:
        throw new BusinessException("库存操作类型无效");
    }
    if (delta == 0) throw new BusinessException("库存数量没有变化");
    if (!Integer.valueOf(1).equals(d.getDelmark()) && delta > 0)
      throw new BusinessException("停用药品不可新增入库");
    changeStock(d, delta, null, dto.getType(), reason);
  }

  @Override
  public PageResult<Map<String, Object>> transactions(PageQuery q) {
    RoleAccess.role("pharmacy");
    if (q.getDate() == null) q.setDate(LocalDate.now(ZoneId.of("Asia/Shanghai")).toString());
    RoleAccess.date(q.getDate());
    return new PageResult<>(mapper.transactions(q), mapper.transactionCount(q), q);
  }
}
