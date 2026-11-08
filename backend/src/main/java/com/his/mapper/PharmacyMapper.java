package com.his.mapper;

import com.his.entity.*;
import com.his.outpatient.dto.PageQuery;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface PharmacyMapper {
  List<Map<String, Object>> prescriptions(PageQuery q);

  int prescriptionCount(PageQuery q);

  Prescription prescription(Integer id);

  Integer prescriptionVisit(Integer id);

  int insertPrescription(Prescription p);

  int state(
      @Param("id") Integer id, @Param("expected") String expected, @Param("next") String next);

  List<FeeRecord> effectiveCharges(
      @Param("registerId") Integer registerId,
      @Param("type") String type,
      @Param("itemId") Integer itemId);

  DrugInfo drug(Integer id);

}
