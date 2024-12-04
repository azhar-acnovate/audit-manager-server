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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		commonResponse.setMessage("Scheduling report with Report ID " + request.getReportIds() + " saved successfully");
		commonResponse.setData(schedulingAuditReportService.createSchedulingAuditReport(request));
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/cancel/{id}")
	public ResponseEntity<CommonResponse> cancelReport(@PathVariable Long id) {
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setStatus(HttpStatus.OK.value());
		commonResponse.setMessage("Scheduling report cancelled successfully");
		schedulingAuditReportService.cancelReport(id);
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/reschdule/{id}")
	public ResponseEntity<CommonResponse> reschduleReport(@PathVariable Long id) {
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setStatus(HttpStatus.OK.value());
		commonResponse.setMessage("Scheduling report reschduled successfully");
		schedulingAuditReportService.reschduleReport(id);
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo) {
		CommonResponse commonResponse = new CommonResponse();
		FilterDto filter = new FilterDto(); // Make sure this is set correctly
		filter.getSort().put("updatedAt", "desc");
		if (size != null && pageNo != null) {

			Page<SchedulingAuditReport> pages = schedulingAuditReportService.findAll(size, pageNo, filter);
			commonResponse.setData(pages.map(schedulingAuditReportService::domainToDto));
		} else {
			List<SchedulingAuditReport> list = schedulingAuditReportService.findAll(filter, true);
			commonResponse.setData(list.stream().map(schedulingAuditReportService::domainToDto).toList());

		}
		commonResponse.setStatus(HttpStatus.OK.value());
		commonResponse.setMessage("Successfully fetched user data.");
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}
}
