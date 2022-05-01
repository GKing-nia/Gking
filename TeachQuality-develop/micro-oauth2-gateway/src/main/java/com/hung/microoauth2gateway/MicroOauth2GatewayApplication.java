package com.hung.microoauth2gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayAutoConfiguration;

/**
 * @author Hung
 */
@SpringBootApplication(exclude = TokenRelayAutoConfiguration.class)
@EnableDiscoveryClient
public class MicroOauth2GatewayApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MicroOauth2GatewayApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }

}
