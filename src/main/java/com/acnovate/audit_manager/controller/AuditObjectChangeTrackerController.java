package com.acnovate.audit_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.domain.AuditObjectChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditObjectChangeRequestDto;
import com.acnovate.audit_manager.service.IAuditObjectChangeTrackerService;

@RestController
@RequestMapping("audit-object-change-tracker")
@CrossOrigin("*")
public class AuditObjectChangeTrackerController {

	@Autowired
	private IAuditObjectChangeTrackerService auditObjectChangeTrackerService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo) {
		CommonResponse res = new CommonResponse();

		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched audit-module Data..");
		if (size != null & pageNo != null) {
			FilterDto filter = new FilterDto();
			Page<AuditObjectChangeTracker> pages = auditObjectChangeTrackerService.findAll(size, pageNo, filter);
			res.setData(pages.map(auditObjectChangeTrackerService::domainToDto));
		} else {
			List<AuditObjectChangeTracker> list = auditObjectChangeTrackerService.findAll();
			res.setData(list.stream().map(auditObjectChangeTrackerService::domainToDto).toList());

		}
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CommonResponse> createAuditObjectChangeTracker(
			@RequestBody AuditObjectChangeRequestDto auditObjectChangeRequestDto) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully Created Audit Object Change Tracker");
		res.setData(auditObjectChangeTrackerService.createAuditObjectChangeTracker(auditObjectChangeRequestDto));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}
}
