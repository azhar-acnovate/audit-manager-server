package com.acnovate.audit_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.domain.AuditReport;
import com.acnovate.audit_manager.dto.request.AuditReportRequestDto;
import com.acnovate.audit_manager.service.IAuditReportService;

@RestController
@RequestMapping(value = "audit-report")
public class AuditReportController {
	@Autowired
	private IAuditReportService auditReportService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched auditReport Data..");
		if (size != null & pageNo != null) {
			FilterDto filter = new FilterDto();
			Page<AuditReport> pages = auditReportService.findAll(size, pageNo, filter);
			res.setData(pages.map(auditReportService::domainToDto));
		} else {
			List<AuditReport> list = auditReportService.findAll();
			res.setData(list.stream().map(auditReportService::domainToDto).toList());

		}
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findOne(@PathVariable("id") final Long id) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully Fetched Audit Report Data..");
		res.setData(auditReportService.domainToDto(auditReportService.findOne(id)));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommonResponse> create(@RequestBody final AuditReportRequestDto req) {

		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully " + (req.getId() != null ? "Updated" : "Create") + " Audit Object Change Tracker");
		res.setData(auditReportService.createAuditReport(req));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(value = "/generate-report", method = RequestMethod.GET)
	public ResponseEntity<byte[]> generateFile(@RequestParam String fileType, @RequestParam List<Long> reportIds) {
		byte[] fileBytes = auditReportService.genereteReport(auditReportService.getLoggedUserId(), reportIds, fileType);
		HttpHeaders headersResponse = new HttpHeaders();
		headersResponse.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "report." + fileType);

		String mediaType = fileType.equalsIgnoreCase("csv") ? "text/csv"
				: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		headersResponse.set(HttpHeaders.CONTENT_TYPE, mediaType);

		return ResponseEntity.ok().headers(headersResponse).body(fileBytes);
	}

}
