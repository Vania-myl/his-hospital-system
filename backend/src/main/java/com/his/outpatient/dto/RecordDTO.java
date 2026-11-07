package com.his.outpatient.dto;

import com.his.entity.MedicalRecord;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordDTO extends MedicalRecord {
  private List<Integer> diseaseIds;
}
