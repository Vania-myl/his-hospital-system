package com.his.inspection.service;

import com.his.inspection.dto.InspectionPageVO;
import com.his.inspection.dto.InspectionQueryDTO;
import com.his.inspection.dto.InspectionResultDTO;

public interface InspectionRequestService {
    InspectionPageVO list(InspectionQueryDTO query);
    void assign(Integer id);
    void saveResult(Integer id, InspectionResultDTO dto);
}
