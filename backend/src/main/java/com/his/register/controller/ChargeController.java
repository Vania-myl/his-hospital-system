package com.his.register.controller;

import com.his.common.Result;
import com.his.entity.FeeRecord;
import com.his.register.dto.ChargeDTO;
import com.his.register.dto.ChargeItemVO;
import com.his.register.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charge")
public class ChargeController {

    @Autowired
    private ChargeService chargeService;

    @GetMapping("/pending")
    public Result<List<ChargeItemVO>> pending(@RequestParam String caseNumber) {
        return Result.success(chargeService.pending(caseNumber));
    }

    @PostMapping
    public Result<List<FeeRecord>> charge(@RequestBody ChargeDTO dto) {
        return Result.success(chargeService.charge(dto));
    }
}
