package com.acnovate.audit_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.acnovate.audit_manager.common.persistence.specification.RawSpecification;
import com.acnovate.audit_manager.domain.SourceReferenceObject;
import com.acnovate.audit_manager.dto.request.SourceReferenceObjectRequestDto;
import com.acnovate.audit_manager.service.ISourceReferenceObjectService;

@RestController
@RequestMapping(value = "source-reference-object")
public class SourceReferenceObjectController {
	@Autowired
	private ISourceReferenceObjectService sourceReferenceObjectService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) String inputValue) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched Source Reference Object Data..");
		FilterDto filter = new FilterDto();
		filter.getSort().put("updatedAt", "desc");
		if (inputValue != null && !inputValue.equals("null")) {
			if (inputValue.contains("-")) {
				String[] inputValueArg = inputValue.split("-");
				filter.getFilter().put("sourceReferenceName", inputValueArg[0] + RawSpecification.CRITERIA_LIKE);
				if (inputValueArg.length > 1) {
					filter.getFilter().put("sourceReferenceKey", inputValueArg[1] + RawSpecification.CRITERIA_LIKE);
				}
			} else {
				filter.getFilter().put("sourceReferenceName" + RawSpecification.CRITERIA_OR,
						inputValue + RawSpecification.CRITERIA_LIKE);
				filter.getFilter().put("sourceReferenceKey", inputValue + RawSpecification.CRITERIA_LIKE);
			}
		}
		if (size != null && pageNo != null) {

			Page<SourceReferenceObject> pages = sourceReferenceObjectService.findAll(size, pageNo, filter);
			res.setData(pages.map(sourceReferenceObjectService::domainToDto));
		} else {
			List<SourceReferenceObject> list = sourceReferenceObjectService.findAll(filter);
			res.setData(list);
		}
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findOne(@PathVariable("id") final Long id) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully Fetched Source Reference Object Data..");
		res.setData(sourceReferenceObjectService.domainToDto(sourceReferenceObjectService.findOne(id)));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommonResponse> create(@RequestBody final SourceReferenceObjectRequestDto req) {

		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully Created Source Reference Object");
		res.setData(sourceReferenceObjectService.createSourceReferenceObject(req));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

}
