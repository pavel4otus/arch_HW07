package ru.pavel2107.arch.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.admin.domain.catalog.Good;
import ru.pavel2107.arch.admin.service.GoodsService;

import java.util.List;

@RestController
public class GoodsController {
    static final Logger logger = LogManager.getLogger(GoodsController.class);

    private GoodsService service;

    @Autowired
    public GoodsController(GoodsService service) {
        this.service = service;
    }

    @GetMapping(value = "/microservices/v1/admin/goods", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Good> listGoods() {
        logger.info( "GOODS. список товаров");
        List<Good> list = service.findAll();
        return list;
    }

    @DeleteMapping(value = "/microservices/v1/admin/goods")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestParam(value = "id") Long id) {
        logger.info( "GOODS. DELETE: {}", id);
        service.delete(id);
    }

    @GetMapping(value = "/microservices/v1/admin/goods/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Good get(@PathVariable(value = "id") Long id) {
        logger.info( "GOODS. FIND: {}", id);
        Good good = service.find(id);
        return good;
    }

    @PutMapping(value = "/microservices/v1/admin/goods", produces = MediaType.APPLICATION_JSON_VALUE)
    public Good save(@RequestBody Good good) {
        logger.info( "GOODS. SAVE: {}", good.getSkud());
        Good newGood = service.save(good);
        return newGood;
    }

}
