package com.acnovate.audit_manager.controller;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.domain.SchedulingAuditReport;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduling-audit-report")
@Validated
public class SchedulingAuditReportController {

  @Autowired
  private ISchedulingAuditReportService schedulingAuditReportService;

  @PostMapping
  @ResponseBody
  public ResponseEntity<CommonResponse> createReport(@Valid @RequestBody SchedulingAuditReportRequest request) {
    CommonResponse res = new CommonResponse();

    res.setStatus(HttpStatus.OK.value());
    res.setMessage("Scheduling report with Report ID " + request.getReportId() + " saved successfully");
    res.setData(schedulingAuditReportService.createSchedulingAuditReport(request));
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer pageNo) {
    CommonResponse response = new CommonResponse();

    // If size and pageNo are provided, return paginated results
    if (size != null && pageNo != null) {
      Page<SchedulingAuditReport> paginatedReports = schedulingAuditReportService.findAll(size, pageNo);
      response.setStatus(HttpStatus.OK.value());
      response.setMessage("Successfully fetched paginated scheduling audit reports.");
      response.setData(paginatedReports.getContent());
    } else {
      // If no pagination parameters are provided, return all results
      List<SchedulingAuditReport> reports = schedulingAuditReportService.findAll();
      response.setStatus(HttpStatus.OK.value());
      response.setMessage("Successfully fetched all scheduling audit reports.");
      response.setData(reports);
    }

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
