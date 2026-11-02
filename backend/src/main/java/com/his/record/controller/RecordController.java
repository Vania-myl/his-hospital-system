package com.his.record.controller;

import com.his.common.Result;
import com.his.record.dto.RecordFlowDTO;
import com.his.record.dto.RecordHistoryPageVO;
import com.his.record.dto.RecordQueryDTO;
import com.his.record.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    /** 历史业务查询（分页） */
    @GetMapping("/history")
    public Result<RecordHistoryPageVO> history(RecordQueryDTO query) {
        return Result.success(recordService.history(query));
    }

    /** 就诊全流程记录 */
    @GetMapping("/flow")
    public Result<RecordFlowDTO> flow(@RequestParam(required = false) Integer registerId,
                                      @RequestParam(required = false) String caseNumber) {
        return Result.success(recordService.flow(registerId, caseNumber));
    }
}
