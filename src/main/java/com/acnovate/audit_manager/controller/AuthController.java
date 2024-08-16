package com.acnovate.audit_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acnovate.audit_manager.security.TokenService;

@RestController
@RequestMapping("auth")
@CrossOrigin("*")
public class AuthController {
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	@GetMapping("/token")
	public ResponseEntity<?> token(Authentication authentication) {

		return new ResponseEntity<>(tokenService.generateToken(authentication), HttpStatus.OK);

	}

	@PostMapping("/generate-password")
	public ResponseEntity<String> generatePassword(@RequestBody String rawPassword) {
		return new ResponseEntity<>(passwordEncoder.encode(rawPassword), HttpStatus.OK);
	}
}
