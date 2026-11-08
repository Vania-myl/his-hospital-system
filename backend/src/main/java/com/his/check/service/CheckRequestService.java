package com.his.check.service;

import com.his.check.dto.CheckPageVO;
import com.his.check.dto.CheckQueryDTO;
import com.his.check.dto.CheckResultDTO;

public interface CheckRequestService {
    CheckPageVO list(CheckQueryDTO query);
    void assign(Integer id);
    void saveResult(Integer id, CheckResultDTO dto);
}
