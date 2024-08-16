package com.acnovate.audit_manager.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuditorAware implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.of(0L);
		}
		// In CustomUserDetailsService we have handle userName with userId
		Long userId = Long.parseLong(authentication.getName().split("_")[1]);

		return Optional.of(userId);
	}

}
