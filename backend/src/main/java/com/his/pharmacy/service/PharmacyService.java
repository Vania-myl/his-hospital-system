package com.his.pharmacy.service;

import com.his.entity.*;
import com.his.outpatient.dto.*;
import com.his.pharmacy.dto.StockDTO;
import java.util.Map;

public interface PharmacyService {
  PageResult<Map<String, Object>> prescriptions(PageQuery query);

  Prescription prescribe(Prescription p);

  void cancel(Integer id);

  void dispense(Integer id);

  void returnDrug(Integer id, String reason);

  PageResult<DrugInfo> drugs(PageQuery q);

  DrugInfo saveDrug(Integer id, DrugInfo d);

  void disable(Integer id);

  void stock(Integer id, StockDTO dto);

  PageResult<Map<String, Object>> transactions(PageQuery q);
}
