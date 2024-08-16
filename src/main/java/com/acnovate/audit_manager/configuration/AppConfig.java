package com.acnovate.audit_manager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "com.acnovate.audit_manager")
@EnableWebMvc
public class AppConfig {

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new SpringSecurityAuditorAware();
	}
}