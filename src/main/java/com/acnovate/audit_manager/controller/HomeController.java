package com.acnovate.audit_manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("home")
@CrossOrigin("*")
public class HomeController {

	@GetMapping("/hello")
	public ResponseEntity<?> hello(Authentication authentication) {

		return new ResponseEntity<>("Hello " + authentication.getName(), HttpStatus.OK);

	}
}
