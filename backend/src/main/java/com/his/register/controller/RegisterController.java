package com.his.register.controller;

import com.his.common.Result;
import com.his.entity.Register;
import com.his.register.dto.RegisterDTO;
import com.his.register.dto.RegisterQueryDTO;
import com.his.register.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public Result<Register> register(@RequestBody RegisterDTO dto) {
        return Result.success(registerService.register(dto));
    }

    @GetMapping("/list")
    public Result<List<Register>> list(RegisterQueryDTO query) {
        return Result.success(registerService.list(query));
    }

    @GetMapping("/count")
    public Result<Integer> count(RegisterQueryDTO query) {
        return Result.success(registerService.count(query));
    }

    @PostMapping("/refund/{id}")
    public Result<Void> refund(@PathVariable Integer id) {
        registerService.refund(id);
        return Result.success();
    }
}
