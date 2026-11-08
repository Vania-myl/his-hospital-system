package com.his.pharmacy.controller;

import com.his.common.Result;
import com.his.entity.*;
import com.his.outpatient.dto.*;
import com.his.pharmacy.service.PharmacyService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PharmacyController {
  private final PharmacyService service;

  @GetMapping("/prescription/list")
  public Result<PageResult<Map<String, Object>>> list(PageQuery q) {
    return Result.success(service.prescriptions(q));
  }

  @PostMapping("/prescription")
  public Result<Prescription> prescribe(@RequestBody Prescription p) {
    return Result.success(service.prescribe(p));
  }

  @PutMapping("/prescription/{id}/cancel")
  public Result<Void> cancel(@PathVariable Integer id) {
    service.cancel(id);
    return Result.success();
  }

}
