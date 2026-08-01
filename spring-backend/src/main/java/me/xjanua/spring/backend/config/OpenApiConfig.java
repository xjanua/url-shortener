package me.xjanua.spring.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
        private SecurityScheme createAPIKeyScheme() {
                return new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer");
        }

        @Value("${swagger.server-url}")
        private String swaggerServerUrl;

        @Value("${swagger.server-desc}")
        private String swaggerServerDesc;

        private Contact createContact() {
                return new Contact()
                                .email("ktoannguyen.dev@gmail.com") // Cập nhật email hỗ trợ dự án
                                .name("Nguyễn Kim Toàn") // Tên nhóm phát triển
                                .url("https://www.facebook.com/Ktoan2209"); // URL dự án (nếu có)
        }

        private License createLicense() {
                return new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/");
        }

        private Info createApiInfo() {
                return new Info()
                                .title("Portfolio API") // Tên API
                                .version("1.0")
                                .contact(createContact())
                                .description("API cho hệ thống Portfolio của Nguyễn Kim Toàn.\n")
                                .termsOfService("#")
                                .license(createLicense());
        }

        @Bean
        public OpenAPI myOpenAPI() {
                return new OpenAPI()
                                .info(createApiInfo())
                                .servers(List.of(new Server()
                                                .url(swaggerServerUrl)
                                                .description(swaggerServerDesc)))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
        }
}
