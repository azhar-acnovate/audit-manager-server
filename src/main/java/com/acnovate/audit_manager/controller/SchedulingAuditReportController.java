package com.acnovate.audit_manager.controller;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.dto.request.SchedulingAuditReportRequest;
import com.acnovate.audit_manager.service.ISchedulingAuditReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
}
