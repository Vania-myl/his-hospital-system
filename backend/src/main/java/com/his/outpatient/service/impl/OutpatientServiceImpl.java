package com.his.outpatient.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.his.common.exception.BusinessException;
import com.his.common.util.RoleAccess;
import com.his.entity.*;
import com.his.mapper.*;
import com.his.outpatient.dto.*;
import com.his.outpatient.service.*;
import com.his.register.service.VisitLifecycleService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
public class OutpatientServiceImpl implements OutpatientService {
  private final OutpatientMapper mapper;
  private final RegisterMapper registers;
  private final BusinessLockMapper locks;
  private final PharmacyMapper pharmacy;
  private final MedicalTechnologyMapper technologies;
  private final VisitLifecycleService lifecycle;
  private final ObjectMapper json;

  private Register owned(Integer id, boolean editing) {
    Integer doctor = RoleAccess.role("doctor").getId();
    Register r = registers.selectById(id);
    if (r == null) throw new BusinessException("挂号记录不存在");
    if (!doctor.equals(r.getEmployeeId())) throw new BusinessException(403, "只能处理本人接诊患者");
    if (editing) {
      r = lifecycle.owned(id);
      if (!Integer.valueOf(2).equals(r.getVisitState()))
        throw new BusinessException("请先开始接诊；结束或退号后不能继续编辑");
    }
    return r;
  }

  @Override
  public PageResult<Map<String, Object>> waiting(PageQuery q) {
    Integer doctor = RoleAccess.role("doctor").getId();
    if (q.getDate() == null) q.setDate(LocalDate.now(ZoneId.of("Asia/Shanghai")).toString());
    RoleAccess.date(q.getDate());
    return new PageResult<>(mapper.waiting(doctor, q), mapper.waitingCount(doctor, q), q);
  }

  @Override
  public void queue(Integer id, String state) {
    Register r = lifecycle.owned(id);
    if (!Integer.valueOf(1).equals(r.getVisitState())) throw new BusinessException("只有候诊患者可以叫号或过号");
    if (!"called".equals(state) && !"skipped".equals(state)) throw new BusinessException("候诊状态无效");
    mapper.queue(id, state, RoleAccess.role("doctor").getId());
  }

  @Override
  public void start(Integer id) {
    lifecycle.start(id);
  }

  @Override
  public void finish(Integer id) {
    owned(id, true);
    MedicalRecord r = mapper.record(id);
    if (r == null || !Integer.valueOf(1).equals(r.getDiagnosisState()))
      throw new BusinessException("请先保存病历并完成确诊");
    lifecycle.finish(id);
  }

  @Override
  public RecordDTO record(Integer id) {
    owned(id, false);
    MedicalRecord r = mapper.record(id);
    if (r == null) return null;
    RecordDTO result = new RecordDTO();
    BeanUtils.copyProperties(r, result);
    result.setDiseaseIds(mapper.diseaseIds(r.getId()));
    return result;
  }

  private MedicalRecord requireRecord(Integer id, boolean editing) {
    MedicalRecord r = mapper.recordById(id);
    if (r == null) throw new BusinessException("病历不存在");
    owned(r.getRegisterId(), editing);
    return r;
  }

  private void validate(RecordDTO r) {
    r.setReadme(RoleAccess.text(r.getReadme(), "主诉", 512, true));
    r.setPresent(RoleAccess.text(r.getPresent(), "现病史", 512, true));
    r.setHistory(RoleAccess.text(r.getHistory(), "既往史", 512, true));
    r.setAllergy(RoleAccess.text(r.getAllergy(), "过敏史", 512, true));
    r.setPhysique(RoleAccess.text(r.getPhysique(), "体格检查", 512, true));
    r.setPresentTreat(RoleAccess.text(r.getPresentTreat(), "现病治疗情况", 512, false));
    r.setProposal(RoleAccess.text(r.getProposal(), "检查建议", 512, false));
    r.setCareful(RoleAccess.text(r.getCareful(), "注意事项", 512, false));
    r.setCure(RoleAccess.text(r.getCure(), "处理意见", 512, false));
  }

  private void checkVersion(MedicalRecord r, RecordDTO dto) {
    if (dto.getVersion() == null || !dto.getVersion().equals(r.getVersion()))
      throw new BusinessException("病历已有更新，请重新加载后再保存");
  }

  private void revision(MedicalRecord r) {
    RecordDTO snapshot = new RecordDTO();
    BeanUtils.copyProperties(mapper.recordById(r.getId()), snapshot);
    snapshot.setDiseaseIds(mapper.diseaseIds(r.getId()));
    try {
      mapper.revision(
          r.getId(),
          snapshot.getVersion(),
          json.writeValueAsString(snapshot),
          RoleAccess.role("doctor").getId());
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new IllegalStateException("病历版本保存失败", e);
    }
  }

  @Override
  public MedicalRecord save(Integer id, RecordDTO dto) {
    if (dto == null) throw new BusinessException("病历内容不能为空");
    MedicalRecord r;
    if (id == null) {
      if (dto.getRegisterId() == null) throw new BusinessException("请选择本次挂号");
      owned(dto.getRegisterId(), true);
      if (mapper.record(dto.getRegisterId()) != null)
        throw new BusinessException("本次就诊已创建病历，请编辑现有病历");
      r = new MedicalRecord();
      r.setRegisterId(dto.getRegisterId());
      r.setVersion(1);
      r.setCreatedBy(RoleAccess.role("doctor").getId());
      r.setDiagnosisState(0);
    } else {
      r = requireRecord(id, true);
      checkVersion(r, dto);
    }
    validate(dto);
    r.setReadme(dto.getReadme());
    r.setPresent(dto.getPresent());
    r.setPresentTreat(dto.getPresentTreat());
    r.setHistory(dto.getHistory());
    r.setAllergy(dto.getAllergy());
    r.setPhysique(dto.getPhysique());
    r.setProposal(dto.getProposal());
    r.setCareful(dto.getCareful());
    r.setCure(dto.getCure());
    r.setUpdatedBy(RoleAccess.role("doctor").getId());
    if (id == null) mapper.insertRecord(r);
    else if (mapper.updateRecord(r) != 1) throw new BusinessException("病历版本冲突，请刷新");
    revision(r);
    return mapper.recordById(r.getId());
  }

  @Override
  public MedicalRecord diagnose(Integer id, RecordDTO dto) {
    if (dto == null) throw new BusinessException("确诊信息不能为空");
    MedicalRecord r = requireRecord(id, true);
    checkVersion(r, dto);
    r.setDiagnosis(RoleAccess.text(dto.getDiagnosis(), "诊断说明", 512, true));
    r.setCure(RoleAccess.text(dto.getCure(), "处理意见", 512, false));
    if (dto.getDiseaseIds() == null
        || dto.getDiseaseIds().isEmpty()
        || dto.getDiseaseIds().size() > 20) throw new BusinessException("请选择1至20个ICD诊断");
    Set<Integer> ids = new LinkedHashSet<>(dto.getDiseaseIds());
    for (Integer disease : ids)
      if (disease == null || mapper.diseaseExists(disease) != 1)
        throw new BusinessException("疾病字典项不存在");
    r.setDiagnosisState(1);
    r.setUpdatedBy(RoleAccess.role("doctor").getId());
    if (mapper.updateRecord(r) != 1) throw new BusinessException("病历版本冲突，请刷新");
    mapper.clearDiseases(id);
    for (Integer disease : ids) mapper.addDisease(id, disease);
    revision(r);
    return mapper.recordById(id);
  }

  @Override
  public List<Map<String, Object>> revisions(Integer id) {
    requireRecord(id, false);
    return mapper.revisions(id);
  }

  private String title(String kind) {
    switch (kind) {
      case "check":
        return "检查";
      case "inspection":
        return "检验";
      case "disposal":
        return "处置";
      default:
        throw new BusinessException("申请类型无效");
    }
  }

  @Override
  public MedicalOrder createOrder(String kind, OrderDTO dto) {
    String title = title(kind);
    if (dto == null || dto.getRegisterId() == null || dto.getMedicalTechnologyId() == null)
      throw new BusinessException("请选择患者和医技项目");
    owned(dto.getRegisterId(), true);
    if (mapper.record(dto.getRegisterId()) == null) throw new BusinessException("请先保存病历");
    MedicalTechnology tech =
        technologies.selectAll().stream()
            .filter(t -> dto.getMedicalTechnologyId().equals(t.getId()))
            .findFirst()
            .orElse(null);
    if (tech == null || !title.equals(tech.getTechType()))
      throw new BusinessException("医技项目不存在或类型不符");
    MedicalOrder order = new MedicalOrder();
    order.setKind(kind);
    order.setRegisterId(dto.getRegisterId());
    order.setMedicalTechnologyId(tech.getId());
    String info = dto.getInfo(), position = dto.getPosition();
    if (info == null)
      info =
          "check".equals(kind)
              ? dto.getCheckInfo()
              : "inspection".equals(kind) ? dto.getInspectionInfo() : dto.getDisposalInfo();
    if (position == null)
      position =
          "check".equals(kind)
              ? dto.getCheckPosition()
              : "inspection".equals(kind) ? dto.getInspectionPosition() : dto.getDisposalPosition();
    order.setInfo(RoleAccess.text(info, title + "目的", 512, true));
    order.setPosition(RoleAccess.text(position, "部位或标本", 255, true));
    mapper.insertOrder(order);
    return order;
  }

  @Override
  public Map<String, Object> order(String kind, Integer id) {
    title(kind);
    RoleAccess.role("doctor");
    Map<String, Object> r = mapper.order(kind, id);
    if (r == null) throw new BusinessException("申请不存在");
    owned(((Number) r.get("registerId")).intValue(), false);
    return r;
  }

  @Override
  public List<Map<String, Object>> orders(Integer id) {
    owned(id, false);
    return mapper.orders(id);
  }

  @Override
  public void cancelOrder(String kind, Integer id) {
    title(kind);
    Map<String, Object> r = order(kind, id);
    owned(((Number) r.get("registerId")).intValue(), true);
    locks.item(title(kind), id);
    r = mapper.order(kind, id);
    if (!"1".equals(r.get("state"))) throw new BusinessException("只有未执行申请可以撤销");
    if (!pharmacy
        .effectiveCharges(((Number) r.get("registerId")).intValue(), title(kind), id)
        .isEmpty()) throw new BusinessException("已缴费申请须先到窗口退费");
    if (mapper.cancelOrder(kind, id) != 1) throw new BusinessException("申请状态已变化，请刷新");
  }

  @Override
  public Map<String, Object> fees(Integer id) {
    owned(id, false);
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("items", mapper.feeItems(id));
    result.put("records", mapper.fees(id));
    return result;
  }
}
