package com.example.abb.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(title = "ABB - IOT", version = "1.0", description = "ABB - IOT PROJECT"))
public class OpenAPIConfig {
    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .servers(servers());

    }
    public List<Server> servers() {

        Server server = new Server();
        server.setUrl("/");
        return Collections.singletonList(server);

    }
}
