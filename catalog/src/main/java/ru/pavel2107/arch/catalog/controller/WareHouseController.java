package ru.pavel2107.arch.catalog.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.catalog.domain.WareHouse;
import ru.pavel2107.arch.catalog.service.WareHouseService;

import java.util.List;

@RestController
public class WareHouseController {
    static final Logger logger = LogManager.getLogger(WareHouseController.class);

    private WareHouseService service;

    @Autowired
    public WareHouseController(WareHouseService service) {
        this.service = service;
    }

    @GetMapping(value = "/microservices/v2/catalog/warehouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WareHouse> wareHouseList() {
        logger.info( "WAREHOUSES. Список складов");
        List<WareHouse> list = service.findAll();
        return list;
    }
}
