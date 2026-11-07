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

  List<DrugInfo> drugs(PageQuery q);

  int drugCount(PageQuery q);

  int insertDrug(DrugInfo d);

  int updateDrug(DrugInfo d);

  int codeCount(@Param("code") String code, @Param("id") Integer id);

  int pendingCount(Integer drugId);

  int disable(Integer id);

  int stock(@Param("id") Integer id, @Param("delta") int delta);

  int transaction(
      @Param("drugId") Integer drugId,
      @Param("prescriptionId") Integer prescriptionId,
      @Param("type") String type,
      @Param("quantity") int quantity,
      @Param("stockAfter") int stockAfter,
      @Param("reason") String reason,
      @Param("operatorId") Integer operatorId);

  List<Map<String, Object>> transactions(PageQuery q);

  int transactionCount(PageQuery q);
}
