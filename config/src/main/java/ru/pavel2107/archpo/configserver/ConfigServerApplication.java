package ru.pavel2107.archpo.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

    @Bean
    public WebSecurityConfigurerAdapter configurerAdapter(ServerProperties serverProperties) {
        return new WebSecurityConfigurerAdapter() {

            @Override
            protected void configure(HttpSecurity httpSecurity) throws Exception {
                httpSecurity
                        .authorizeRequests()
                        .antMatchers("/actuator/health").permitAll()
                        .antMatchers("**/encrypt/**").authenticated()
                        .antMatchers("**/decrypt/**").authenticated()
                        .and().csrf().disable();
            }
        };
    }
}
