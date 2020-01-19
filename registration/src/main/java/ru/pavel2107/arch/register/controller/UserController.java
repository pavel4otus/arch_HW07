package ru.pavel2107.arch.register.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.register.condig.RabbitConfig;
import ru.pavel2107.arch.register.domain.User;
import ru.pavel2107.arch.register.domain.UserRegistration;
import ru.pavel2107.arch.register.service.UserService;

import java.util.List;

@RestController
public class UserController {
    static final Logger logger = LogManager.getLogger(UserController.class);

    private UserService service;
    private RabbitTemplate rabbitTemplate;


    @Autowired
    public UserController(UserService service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(value = "/microservices/v1/users/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public User register(@RequestBody User user) throws Exception {
        logger.info( "REG. NEW_USER: {} ", user.getEmail());
        User newUser = service.registerNewUser(user);
        UserRegistration userRegistration = new UserRegistration("registered", newUser);
        rabbitTemplate.convertAndSend(RabbitConfig.REGISTRATION_QUEUE, userRegistration);
        return newUser;
    }

    @PutMapping(value = "/microservices/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateExisting(@RequestBody User user) throws Exception {
        logger.info( "REG. UPDATE_USER: {} ", user.getEmail());
        User newUser = service.updateExisting(user);
        UserRegistration userRegistration = new UserRegistration("updated", newUser);
        rabbitTemplate.convertAndSend(RabbitConfig.REGISTRATION_QUEUE, userRegistration);
        return newUser;
    }
}