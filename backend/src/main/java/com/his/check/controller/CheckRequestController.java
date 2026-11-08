package com.his.check.controller;

import com.his.check.dto.CheckPageVO;
import com.his.check.dto.CheckQueryDTO;
import com.his.check.dto.CheckResultDTO;
import com.his.check.service.CheckRequestService;
import com.his.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-request")
public class CheckRequestController {
    @Autowired
    private CheckRequestService service;

    @GetMapping("/list")
    public Result<CheckPageVO> list(CheckQueryDTO query) {
        return Result.success(service.list(query));
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Integer id) {
        service.assign(id);
        return Result.success();
    }

    @PutMapping("/{id}/result")
    public Result<Void> result(@PathVariable Integer id, @RequestBody CheckResultDTO dto) {
        service.saveResult(id, dto);
        return Result.success();
    }
}
