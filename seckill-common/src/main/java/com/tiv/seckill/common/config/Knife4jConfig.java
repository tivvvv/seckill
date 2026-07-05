package com.tiv.seckill.common.config;

import com.tiv.seckill.common.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;

@Configuration
public class Knife4jConfig {

    private static final String DEFAULT_SERVICE_NAME = "seckill-api";

    @Bean
    public OpenAPI seckillOpenAPI(Environment environment) {
        String serviceName = getServiceName(environment);

        SecurityScheme tokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(Constants.TOKEN_HEADER_NAME);

        return new OpenAPI()
                .info(new Info()
                        .title(serviceName + " 接口文档")
                        .description("秒杀系统接口文档")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes(Constants.TOKEN_HEADER_NAME, tokenScheme))
                .addSecurityItem(new SecurityRequirement().addList(Constants.TOKEN_HEADER_NAME));
    }

    @Bean
    public GroupedOpenApi seckillGroupedOpenApi(Environment environment) {
        return GroupedOpenApi.builder()
                .group(getServiceName(environment))
                .packagesToScan("com.tiv.seckill")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenApiCustomiser publicApiCustomiser() {
        return openApi -> {
            if (openApi.getPaths() == null || openApi.getPaths().get("/user/login") == null) {
                return;
            }
            openApi.getPaths().get("/user/login")
                    .readOperations()
                    .forEach(operation -> operation.setSecurity(Collections.emptyList()));
        };
    }

    private String getServiceName(Environment environment) {
        return environment.getProperty("spring.application.name", DEFAULT_SERVICE_NAME);
    }
}
