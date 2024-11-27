package com.acnovate.audit_manager.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@OpenAPIDefinition(
        info = @Info(
                title = "Audit Manager APIs",
                description = "Description",
                summary = "Summary",
                termsOfService = "Terms of Service",
                contact = @Contact(
                        name = "Acnovate Corporation",
                        email = "HelpSupport@Acnovate.com"
                ),
                license = @License(
                        name = "Your License No."
                ),
                version = "1.0.0"
        ),
       security = @SecurityRequirement(name = "basic_auth")
)
@SecurityScheme(
        name = "basic_auth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {
}
