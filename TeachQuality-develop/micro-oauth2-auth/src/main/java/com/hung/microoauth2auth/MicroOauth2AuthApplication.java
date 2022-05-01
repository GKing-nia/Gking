package com.hung.microoauth2auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Hung
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MicroOauth2AuthApplication {

    public static void main(String[] args) {

        SpringApplication.run(MicroOauth2AuthApplication.class, args);
    }

}
