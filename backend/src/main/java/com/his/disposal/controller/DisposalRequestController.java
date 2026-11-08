package com.his.disposal.controller;

import com.his.disposal.dto.DisposalPageVO;
import com.his.disposal.dto.DisposalQueryDTO;
import com.his.disposal.dto.DisposalResultDTO;
import com.his.disposal.service.DisposalRequestService;
import com.his.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/disposal-request")
public class DisposalRequestController {
    @Autowired
    private DisposalRequestService service;

    @GetMapping("/list")
    public Result<DisposalPageVO> list(DisposalQueryDTO query) {
        return Result.success(service.list(query));
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Integer id) {
        service.assign(id);
        return Result.success();
    }

    @PutMapping("/{id}/result")
    public Result<Void> result(@PathVariable Integer id, @RequestBody DisposalResultDTO dto) {
        service.saveResult(id, dto);
        return Result.success();
    }
}
