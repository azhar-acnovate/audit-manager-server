package com.acnovate.audit_manager.configuration;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import java.util.Locale;

@OpenAPIDefinition(
        info = @Info(
                title = "Audit Manager Apis",
                description="Description",
                summary = "summary ",
                termsOfService = "term of Service ",
                contact = @Contact(
                        name= "Acnovate Corporation",
                        email = "HelpSupport@Acnovate.com"

                ),
                license = @License(
                        name ="your Lisencse no."
                ),
                version = "1.0.0"

        )
)
public class OpenApiConfig{


}