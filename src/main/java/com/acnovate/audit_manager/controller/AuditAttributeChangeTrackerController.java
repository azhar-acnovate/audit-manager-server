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
import com.acnovate.audit_manager.domain.AuditAttributeChangeTracker;
import com.acnovate.audit_manager.dto.request.AuditAttributeChangeRequestDto;
import com.acnovate.audit_manager.service.IAuditAttributeChangeTrackerService;

@RestController
@RequestMapping("audit-attibute-change-tracker")
@CrossOrigin("*")
public class AuditAttributeChangeTrackerController {

	@Autowired
	private IAuditAttributeChangeTrackerService attributeChangeTrackerService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo, @RequestParam() Long auditObjectChangeTrackerId) {
		CommonResponse res = new CommonResponse();

		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched audit-module Data..");
		FilterDto filter = new FilterDto();
		filter.getFilter().put("auditObjectChangeTracker.id", auditObjectChangeTrackerId);
		if (size != null & pageNo != null) {

			Page<AuditAttributeChangeTracker> pages = attributeChangeTrackerService.findAll(size, pageNo, filter);
			res.setData(pages.map(attributeChangeTrackerService::domainToDto));
		} else {
			List<AuditAttributeChangeTracker> list = attributeChangeTrackerService.findAll(filter);
			res.setData(list.stream().map(attributeChangeTrackerService::domainToDto).toList());

		}
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CommonResponse> createAuditAttributeChangeTracker(
			@RequestBody AuditAttributeChangeRequestDto auditAttributeChangeRequestDto) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully Created Audit Attribute Change Tracker");
		res.setData(attributeChangeTrackerService.createAuditAttributeChangeTracker(auditAttributeChangeRequestDto));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}
}
