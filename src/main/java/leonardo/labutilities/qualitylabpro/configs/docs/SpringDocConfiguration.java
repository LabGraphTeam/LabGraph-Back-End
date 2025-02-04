package leonardo.labutilities.qualitylabpro.configs.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition
public class SpringDocConfiguration {
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info().title("QualityLab-Pro API").version("2.0").description(
                        "REST API for operations on analytical test specifications and internal "
                                + "control data")
                        .contact(new Contact().name("Leonardo Meireles")
                                .email("leomeireles55@hotmail.com"))
                        .license(new License().name("Apache 2.0")
                                .url("http://labutilities/api/license")));
    }
}
