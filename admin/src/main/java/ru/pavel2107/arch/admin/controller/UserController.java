package ru.pavel2107.arch.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.admin.domain.user.User;
import ru.pavel2107.arch.admin.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@Transactional
public class UserController {
    static final Logger logger = LogManager.getLogger(UserController.class);

    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(value = "microservices/v1/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> listUsers() {
        logger.info( "USERS. список пользователей");
        List<User> list = service.findAll();
        return list;
    }


    @GetMapping(value = "microservices/v1/admin/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable(value = "id") Long id) {
        logger.info( "USERS. GET: {}", id);
        User user = service.find(id);
        return user;
    }

    @GetMapping(value = "microservices/v1/admin/users/findByEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findByEmail(@RequestParam(name = "email") String email) {
        logger.info( "USERS. GET: {}", email);
        return service.findByEmail(email);
    }

    @GetMapping(value = "microservices/v1/admin/users/findByPhone", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findByPhone(@RequestParam(name = "phone") String phone) {
        logger.info( "USERS. GET: {}", phone);
        return service.findByPhone(phone);
    }

    @GetMapping(value = "microservices/v1/admin/users/findByFio", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findByFio(@RequestParam(name = "fio") String fio) {
        logger.info( "USERS. GET: {}", fio);
        return service.findByFio(fio);
    }

    @PutMapping(value = "microservices/v1/admin/users/disable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User disable(@PathVariable(value = "id") Long id) {
        logger.info( "USERS. DISABLE: {}", id);
        User user = service.find(id);
        service.disable(user);
        user = service.save(user);
        return user;
    }

    @PutMapping(value = "microservices/v1/admin/users/enable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User enable(@PathVariable(value = "id") Long id) {
        logger.info( "USERS. ENABLE: {}", id);
        User user = service.find(id);
        service.enable(user);
        user = service.save(user);
        return user;
    }

    @PutMapping(value = "microservices/v1/admin/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateExisting(@RequestBody User user) throws Exception {
        logger.info( "USERS. UPDATE: {}", user.getId());
        user = service.updateExisting(user);
        return user;
    }
}
