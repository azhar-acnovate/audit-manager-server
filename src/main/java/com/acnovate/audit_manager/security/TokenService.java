package com.acnovate.audit_manager.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.acnovate.audit_manager.common.dto.LoggedInUserDetails;
import com.acnovate.audit_manager.service.IUserService;

@Service
public class TokenService {
	private static final int ACCESS_TOKEN_EXPIRE_AT_MINUTES = 15; // Short-lived access token
	private static final int REFRESH_TOKEN_EXPIRE_AT_DAYS = 1; // Longer-lived refresh token

	@Autowired
	private JwtEncoder encoder;

	@Autowired
	private JwtDecoder decoder;

	@Autowired
	private IUserService userService;

	public LoggedInUserDetails generateToken(Authentication authentication) {
		Instant issuedAt = Instant.now();
		Instant expireAt = issuedAt.plus(ACCESS_TOKEN_EXPIRE_AT_MINUTES, ChronoUnit.MINUTES);
		String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		LoggedInUserDetails loggedInUserDetails = userService
				.getLoggedInUserDetails(userService.findOne(Long.parseLong(authentication.getName().split("_")[1])));
		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(issuedAt).expiresAt(expireAt)
				.subject(authentication.getName()).claim("scope", scope).build();
		loggedInUserDetails.setAccessToken(encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
		loggedInUserDetails.setIssuedAt(Date.from(issuedAt));
		loggedInUserDetails.setExpireAt(Date.from(expireAt));

		// Generate Refresh Token
		Instant refreshTokenExpireAt = issuedAt.plus(REFRESH_TOKEN_EXPIRE_AT_DAYS, ChronoUnit.DAYS);
		JwtClaimsSet refreshClaims = JwtClaimsSet.builder().issuer("self").issuedAt(issuedAt)
				.expiresAt(refreshTokenExpireAt).subject(authentication.getName()).claim("type", "refresh_token")
				.build();

		String refreshToken = encoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();
		loggedInUserDetails.setRefreshToken(refreshToken);

		return loggedInUserDetails;
	}

	public String generateAccessTokenFromRefresh(String refreshToken) {

		Jwt jwt = decoder.decode(refreshToken);

		if (!jwt.getClaimAsString("type").equals("refresh_token")) {
			throw new RuntimeException("Invalid refresh token");
		}

		String username = jwt.getSubject();
		Instant issuedAt = Instant.now();
		Instant accessTokenExpireAt = issuedAt.plus(ACCESS_TOKEN_EXPIRE_AT_MINUTES, ChronoUnit.MINUTES);

		JwtClaimsSet accessClaims = JwtClaimsSet.builder().issuer("self").issuedAt(issuedAt)
				.expiresAt(accessTokenExpireAt).subject(username)// Carry
																	// over
																	// the
																	// original
																	// scope
				.build();

		return encoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
	}
}
