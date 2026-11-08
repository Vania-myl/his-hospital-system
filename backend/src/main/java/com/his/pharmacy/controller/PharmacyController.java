package com.his.pharmacy.controller;

import com.his.common.Result;
import com.his.entity.*;
import com.his.outpatient.dto.*;
import com.his.pharmacy.dto.StockDTO;
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

  @PutMapping("/prescription/{id}/dispense")
  public Result<Void> dispense(@PathVariable Integer id) {
    service.dispense(id);
    return Result.success();
  }

  @PutMapping("/prescription/{id}/return")
  public Result<Void> returnDrug(@PathVariable Integer id, @RequestBody Map<String, String> body) {
    service.returnDrug(id, body.get("reason"));
    return Result.success();
  }

  @GetMapping("/pharmacy/drugs")
  public Result<PageResult<DrugInfo>> drugs(PageQuery q) {
    return Result.success(service.drugs(q));
  }

  @PostMapping("/drug")
  public Result<DrugInfo> create(@RequestBody DrugInfo d) {
    return Result.success(service.saveDrug(null, d));
  }

  @PutMapping("/drug/{id}")
  public Result<DrugInfo> update(@PathVariable Integer id, @RequestBody DrugInfo d) {
    return Result.success(service.saveDrug(id, d));
  }

  @DeleteMapping("/drug/{id}")
  public Result<Void> disable(@PathVariable Integer id) {
    service.disable(id);
    return Result.success();
  }

  @PostMapping("/drug/{id}/stock")
  public Result<Void> stock(@PathVariable Integer id, @RequestBody StockDTO dto) {
    service.stock(id, dto);
    return Result.success();
  }

  @GetMapping("/pharmacy/transactions")
  public Result<PageResult<Map<String, Object>>> transactions(PageQuery q) {
    return Result.success(service.transactions(q));
  }
}
