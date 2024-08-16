package com.acnovate.audit_manager.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.service.IUserService;

@Service
public class TokenService {
	private static final int EXPIRE_AT_HOURS = 24;

	@Autowired
	private JwtEncoder encoder;

	@Autowired
	private IUserService userService;

	public LoggedInUserDetails generateToken(Authentication authentication) {
		Instant issuedAt = Instant.now();
		Instant expireAt = issuedAt.plus(EXPIRE_AT_HOURS, ChronoUnit.HOURS);
		String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		LoggedInUserDetails loggedInUserDetails = userService
				.getLoggedInUserDetails(userService.findOne(Long.parseLong(authentication.getName().split("_")[1])));
		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(issuedAt).expiresAt(expireAt)
				.subject(authentication.getName()).claim("scope", scope).build();
		loggedInUserDetails.setAccessToken(encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
		loggedInUserDetails.setIssuedAt(issuedAt);
		loggedInUserDetails.setExpireAt(expireAt);

		return loggedInUserDetails;
	}
}
