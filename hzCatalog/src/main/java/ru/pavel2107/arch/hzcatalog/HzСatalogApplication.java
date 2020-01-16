package ru.pavel2107.arch.hzcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class HzСatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HzСatalogApplication.class, args);
    }


    @Bean
    public WebSecurityConfigurerAdapter configurerAdapter(ServerProperties serverProperties) {
        return new WebSecurityConfigurerAdapter() {

            @Override
            public void configure(WebSecurity webSecurity) {
                webSecurity.ignoring().antMatchers("/microservices/v3");
            }
        };
    }
}




