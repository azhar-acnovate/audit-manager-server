package com.acnovate.audit_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.acnovate.audit_manager.common.dto.CommonResponse;
import com.acnovate.audit_manager.common.dto.FilterDto;
import com.acnovate.audit_manager.domain.User;
import com.acnovate.audit_manager.dto.request.UserRequestDto;
import com.acnovate.audit_manager.service.IUserService;

@RestController
@RequestMapping("user")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private IUserService userService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findAll(@RequestParam(required = false) Integer size,
			@RequestParam(required = false) Integer pageNo) {
		CommonResponse res = new CommonResponse();

		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched user Data..");
		if (size != null & pageNo != null) {
			FilterDto filter = new FilterDto();
			Page<User> pages = userService.findAll(size, pageNo, filter);
			res.setData(pages.map(userService::domainToDto));
		} else {
			List<User> list = userService.findAll();
			res.setData(list.stream().map(userService::domainToDto).toList());

		}
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<CommonResponse> findOne(@PathVariable Long id) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully fetched user data..");
		res.setData(userService.domainToDto(userService.findOne(id)));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CommonResponse> createUser(@RequestBody UserRequestDto userRequestDto) {
		CommonResponse res = new CommonResponse();
		res.setStatus(HttpStatus.OK.value());
		res.setMessage("Successfully " + (userRequestDto.getId() != null ? "Updated" : "Create")
				+ " Audit Object Change Tracker");
		res.setData(userService.createUser(userRequestDto));
		return new ResponseEntity<CommonResponse>(res, HttpStatus.OK);

	}








}
