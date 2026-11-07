package com.his.pharmacy.service;

import com.his.entity.*;
import com.his.outpatient.dto.*;
import java.util.Map;

public interface PharmacyService {
  PageResult<Map<String, Object>> prescriptions(PageQuery query);

  Prescription prescribe(Prescription p);

  void cancel(Integer id);

}
