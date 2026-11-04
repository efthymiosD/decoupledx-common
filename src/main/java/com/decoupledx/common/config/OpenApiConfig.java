package com.decoupledx.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bearer-token security scheme for the OpenAPI docs. Applied only when the
 * application sets {@code app.common.openapi.title}; docs-generated content
 * (title/description) stays application-owned.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.common.openapi", name = "title")
public class OpenApiConfig {

    @Bean
    OpenAPI decoupledxOpenApi(
            @Value("${app.common.openapi.title}") String title,
            @Value("${app.common.openapi.description:}") String description,
            @Value("${app.common.openapi.version:v1}") String version) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList("bearer-jwt")));
    }
}
