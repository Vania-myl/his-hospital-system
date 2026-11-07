package com.his.outpatient.controller;

import com.his.common.Result;
import com.his.entity.MedicalRecord;
import com.his.outpatient.dto.*;
import com.his.outpatient.service.OutpatientService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OutpatientController {
  private final OutpatientService service;

  @GetMapping("/register/waiting")
  public Result<PageResult<Map<String, Object>>> waiting(PageQuery q) {
    return Result.success(service.waiting(q));
  }

  @PutMapping("/outpatient/visits/{id}/call")
  public Result<Void> call(@PathVariable Integer id) {
    service.queue(id, "called");
    return Result.success();
  }

  @PutMapping("/outpatient/visits/{id}/skip")
  public Result<Void> skip(@PathVariable Integer id) {
    service.queue(id, "skipped");
    return Result.success();
  }

  @PutMapping("/outpatient/visits/{id}/start")
  public Result<Void> start(@PathVariable Integer id) {
    service.start(id);
    return Result.success();
  }

  @PutMapping("/outpatient/visits/{id}/finish")
  public Result<Void> finish(@PathVariable Integer id) {
    service.finish(id);
    return Result.success();
  }

  @GetMapping("/medical-record/register/{registerId}")
  public Result<RecordDTO> record(@PathVariable Integer registerId) {
    return Result.success(service.record(registerId));
  }

  @PostMapping("/medical-record")
  public Result<MedicalRecord> create(@RequestBody RecordDTO dto) {
    return Result.success(service.save(null, dto));
  }

  @PutMapping("/medical-record/{id}")
  public Result<MedicalRecord> update(@PathVariable Integer id, @RequestBody RecordDTO dto) {
    return Result.success(service.save(id, dto));
  }

  @PutMapping("/medical-record/{id}/diagnosis")
  public Result<MedicalRecord> diagnose(@PathVariable Integer id, @RequestBody RecordDTO dto) {
    return Result.success(service.diagnose(id, dto));
  }

  @GetMapping("/medical-record/{id}/revisions")
  public Result<List<Map<String, Object>>> revisions(@PathVariable Integer id) {
    return Result.success(service.revisions(id));
  }

  @PostMapping("/check-request")
  public Result<MedicalOrder> check(@RequestBody OrderDTO dto) {
    return Result.success(service.createOrder("check", dto));
  }

  @PostMapping("/inspection-request")
  public Result<MedicalOrder> inspection(@RequestBody OrderDTO dto) {
    return Result.success(service.createOrder("inspection", dto));
  }

  @PostMapping("/disposal-request")
  public Result<MedicalOrder> disposal(@RequestBody OrderDTO dto) {
    return Result.success(service.createOrder("disposal", dto));
  }

  @GetMapping("/check-request/{id}")
  public Result<Map<String, Object>> check(@PathVariable Integer id) {
    return Result.success(service.order("check", id));
  }

  @GetMapping("/inspection-request/{id}")
  public Result<Map<String, Object>> inspection(@PathVariable Integer id) {
    return Result.success(service.order("inspection", id));
  }

  @GetMapping("/outpatient/visits/{id}/requests")
  public Result<List<Map<String, Object>>> orders(@PathVariable Integer id) {
    return Result.success(service.orders(id));
  }

  @PutMapping("/outpatient/requests/{kind}/{id}/cancel")
  public Result<Void> cancel(@PathVariable String kind, @PathVariable Integer id) {
    service.cancelOrder(kind, id);
    return Result.success();
  }

  @GetMapping("/outpatient/visits/{id}/fees")
  public Result<Map<String, Object>> fees(@PathVariable Integer id) {
    return Result.success(service.fees(id));
  }
}
