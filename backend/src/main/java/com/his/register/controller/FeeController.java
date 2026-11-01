package com.his.register.controller;

import com.his.common.Result;
import com.his.register.dto.FeeQueryVO;
import com.his.register.dto.FeeRecordVO;
import com.his.register.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/list")
    public Result<FeeQueryVO> list(@RequestParam String caseNumber) {
        return Result.success(feeService.list(caseNumber));
    }

    @GetMapping("/refundable")
    public Result<List<FeeRecordVO>> refundable(@RequestParam String caseNumber) {
        return Result.success(feeService.refundable(caseNumber));
    }

    @PostMapping("/refund/{id}")
    public Result<Void> refund(@PathVariable Integer id) {
        feeService.refund(id);
        return Result.success();
    }
}
