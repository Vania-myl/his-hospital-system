package com.his.disposal.service;

import com.his.disposal.dto.DisposalPageVO;
import com.his.disposal.dto.DisposalQueryDTO;
import com.his.disposal.dto.DisposalResultDTO;

public interface DisposalRequestService {
    DisposalPageVO list(DisposalQueryDTO query);
    void assign(Integer id);
    void saveResult(Integer id, DisposalResultDTO dto);
}
