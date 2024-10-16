package com.acnovate.audit_manager.controller;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.FilterDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduling-audit-report")
@Validated
public class SchedulingAuditReportController {

  @Autowired
  private ISchedulingAuditReportService schedulingAuditReportService;

  @PostMapping
  @ResponseBody
  public ResponseEntity<CommonResponse> createReport(@Valid @RequestBody SchedulingAuditReportRequest request) {
    CommonResponse commonResponse = new CommonResponse();

    commonResponse.setStatus(HttpStatus.OK.value());
    commonResponse.setMessage("Scheduling report with Report ID " + request.getReportId() + " saved successfully");
    commonResponse.setData(schedulingAuditReportService.createSchedulingAuditReport(request));
    return new ResponseEntity<>(commonResponse, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer pageNo) {
    CommonResponse commonResponse = new CommonResponse();

    commonResponse.setStatus(HttpStatus.OK.value());
    commonResponse.setMessage("Successfully fetched user Data..");
    if (size != null & pageNo != null) {
      FilterDto filter = new FilterDto();
      Page<SchedulingAuditReport> pages = schedulingAuditReportService.findAll(size, pageNo, filter);
      commonResponse.setData(pages.map(schedulingAuditReportService::domainToDto));

    } else {
      List<SchedulingAuditReport> list = schedulingAuditReportService.findAll();
      commonResponse.setData(list.stream()
          .map(schedulingAuditReportService::domainToDto)
          .toList());

    }
    return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

  }
}
