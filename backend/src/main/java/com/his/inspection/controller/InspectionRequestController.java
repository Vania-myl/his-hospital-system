package com.his.inspection.controller;

import com.his.inspection.dto.InspectionPageVO;
import com.his.inspection.dto.InspectionQueryDTO;
import com.his.inspection.dto.InspectionResultDTO;
import com.his.inspection.service.InspectionRequestService;
import com.his.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inspection-request")
public class InspectionRequestController {
    @Autowired
    private InspectionRequestService service;

    @GetMapping("/list")
    public Result<InspectionPageVO> list(InspectionQueryDTO query) {
        return Result.success(service.list(query));
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Integer id) {
        service.assign(id);
        return Result.success();
    }

    @PutMapping("/{id}/result")
    public Result<Void> result(@PathVariable Integer id, @RequestBody InspectionResultDTO dto) {
        service.saveResult(id, dto);
        return Result.success();
    }
}
