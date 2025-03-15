package leonardo.labutilities.qualitylabpro.configs.docs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@Profile({"dev", "local"})
public class SpringDocConfiguration {

        @Value("${spring.application.name: LabGraph}")
        private String applicationName;

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI().info(new Info().title(this.applicationName + " API").version("1.0")
                                .description("""
                                                REST API for Laboratory Internal Quality Control.
                                                This API helps clinical and research laboratories monitor and control their process quality.

                                                Key features:
                                                * Manage control standards
                                                * Track test results
                                                * Perform statistical analysis
                                                * Generate quality reports
                                                """)
                                .contact(new Contact().name("Leonardo Meireles").email("leomeireles55@hotmail.com")
                                                .url("https://github.com/LabGraphTeam"))
                                .license(new License().name("GPL 3.0").url(
                                                "https://github.com/LabGraphTeam/LabGraph-Back-End/blob/main/LICENSE")))
                                .servers(List.of(new Server().url("http://localhost:8080")))
                                .components(new Components().addSecuritySchemes("bearer-key",
                                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Use JWT token for authentication")));
        }
}
