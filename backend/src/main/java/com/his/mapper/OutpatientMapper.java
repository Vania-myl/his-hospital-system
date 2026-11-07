package com.his.mapper;

import com.his.entity.MedicalRecord;
import com.his.outpatient.dto.*;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface OutpatientMapper {
  List<Map<String, Object>> waiting(@Param("doctorId") Integer doctorId, @Param("q") PageQuery q);

  int waitingCount(@Param("doctorId") Integer doctorId, @Param("q") PageQuery q);

  int queue(
      @Param("registerId") Integer id,
      @Param("state") String state,
      @Param("operatorId") Integer operatorId);

  MedicalRecord record(Integer registerId);

  MedicalRecord recordById(Integer id);

  int insertRecord(MedicalRecord record);

  int updateRecord(MedicalRecord record);

  int clearDiseases(Integer recordId);

  int addDisease(@Param("recordId") Integer recordId, @Param("diseaseId") Integer diseaseId);

  List<Integer> diseaseIds(Integer recordId);

  int diseaseExists(Integer diseaseId);

  int revision(
      @Param("recordId") Integer recordId,
      @Param("version") Integer version,
      @Param("content") String content,
      @Param("operatorId") Integer operatorId);

  List<Map<String, Object>> revisions(Integer recordId);

  int insertOrder(MedicalOrder order);

  Map<String, Object> order(@Param("kind") String kind, @Param("id") Integer id);

  List<Map<String, Object>> orders(Integer registerId);

  int cancelOrder(@Param("kind") String kind, @Param("id") Integer id);

  List<Map<String, Object>> fees(Integer registerId);

  List<Map<String, Object>> feeItems(Integer registerId);
}
