package ru.pavel2107.arch.register.condig;

import  org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String REGISTRATION_QUEUE = "registration";

    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, false);
    }
}
