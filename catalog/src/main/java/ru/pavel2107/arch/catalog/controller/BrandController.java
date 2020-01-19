package ru.pavel2107.arch.catalog.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pavel2107.arch.catalog.domain.Brand;
import ru.pavel2107.arch.catalog.service.BrandService;

import java.util.List;

@RestController
public class BrandController {
    static final Logger logger = LogManager.getLogger(BrandController.class);

    private BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping(value = "/microservices/v2/catalog/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Brand> getBrands() {
        logger.info( "BRANDS. Список брендов");
        List<Brand> list = brandService.findAll();
        return list;
    }


}
