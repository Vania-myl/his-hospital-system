package com.his.outpatient.service;

import com.his.entity.MedicalRecord;
import com.his.outpatient.dto.*;
import java.util.List;
import java.util.Map;

public interface OutpatientService {
  PageResult<Map<String, Object>> waiting(PageQuery query);

  void queue(Integer registerId, String state);

  void start(Integer registerId);

  void finish(Integer registerId);

  RecordDTO record(Integer registerId);

  MedicalRecord save(Integer id, RecordDTO dto);

  MedicalRecord diagnose(Integer id, RecordDTO dto);

  List<Map<String, Object>> revisions(Integer id);

  MedicalOrder createOrder(String kind, OrderDTO dto);

  Map<String, Object> order(String kind, Integer id);

  List<Map<String, Object>> orders(Integer registerId);

  void cancelOrder(String kind, Integer id);

  Map<String, Object> fees(Integer registerId);
}
