package com.his.register.service;

import com.his.common.exception.BusinessException;
import com.his.common.util.RoleAccess;
import com.his.entity.Register;
import com.his.mapper.BusinessLockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitLifecycleService {
  private final BusinessLockMapper locks;

  @Transactional
  public Register owned(Integer id) {
    Integer doctorId = RoleAccess.role("doctor").getId();
    Register r = locks.register(id);
    if (r == null) throw new BusinessException("挂号记录不存在");
    if (!doctorId.equals(r.getEmployeeId())) throw new BusinessException(403, "只能处理本人接诊患者");
    return r;
  }

  @Transactional
  public void start(Integer id) {
    Register r = owned(id);
    if (Integer.valueOf(2).equals(r.getVisitState())) return;
    if (!Integer.valueOf(1).equals(r.getVisitState())) throw new BusinessException("当前挂号状态不允许接诊");
    if (locks.visit(id, 1, 2) != 1) throw new BusinessException("状态已变化，请刷新");
  }

  @Transactional
  public void finish(Integer id) {
    Register r = owned(id);
    if (!Integer.valueOf(2).equals(r.getVisitState()))
      throw new BusinessException("只有接诊中的患者可以结束看诊");
    if (locks.visit(id, 2, 3) != 1) throw new BusinessException("状态已变化，请刷新");
  }
}
