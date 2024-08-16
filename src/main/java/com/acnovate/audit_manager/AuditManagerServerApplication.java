package com.acnovate.audit_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.acnovate.audit_manager.security.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableJpaAuditing
@PropertySource({ "classpath:${envTarget:dev}.properties" })
public class AuditManagerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditManagerServerApplication.class, args);
	}

}
