package ru.pavel2107.arch.notification.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.pavel2107.arch.notification.service.NotificationService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class NotificationController {
    static final Logger logger = LogManager.getLogger(NotificationController.class);

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @PostMapping(value = "/microservices/v1/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void send(Principal principal, @RequestBody Map<String, String> properties) {
        String destination = properties.get("destination");
        logger.info( "NOTIFICATION. USER: {} DESTINATION: {}", principal.getName(), destination);
        notificationService.send(properties);
    }
}

