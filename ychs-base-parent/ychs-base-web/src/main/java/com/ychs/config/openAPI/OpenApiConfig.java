package com.ychs.config.openAPI;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customizeOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("通用权限项目")
                            .description("通用权限管理系统")
                            .version("v1"));
    }
}