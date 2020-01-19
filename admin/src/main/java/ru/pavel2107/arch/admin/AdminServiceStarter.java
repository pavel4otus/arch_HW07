package ru.pavel2107.arch.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableDiscoveryClient
public class AdminServiceStarter {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceStarter.class, args);
    }
}
