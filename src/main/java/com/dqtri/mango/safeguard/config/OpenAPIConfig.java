package com.dqtri.mango.safeguard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@SecurityScheme(
        name = "refresh_token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
@SecurityScheme(
        name = "access_token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Safeguard Spring API", version = "v1.0.0"))
public class OpenAPIConfig {
    @Bean
    public OpenAPI safeguardOpenAPI() {
        return new OpenAPI()
//                .servers(List.of(new Server().url("http://localhost:8125//safeguard/api")))
                .info(new Info()
                        .title("Safeguard API")
                        .description("Safeguard Spring sample application")
                        .contact(new Contact().email("w.dquoctri@gmail.com")
                                .name("Deadl!ne")
                                .url("https://dqtri.com/me"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Safeguard Wiki Documentation")
                        .url("https://github.com/dquoctri/SafeguardSpring/wiki"));
    }
}
