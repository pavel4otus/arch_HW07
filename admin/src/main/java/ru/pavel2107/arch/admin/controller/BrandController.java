package ru.pavel2107.arch.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.admin.domain.catalog.Brand;
import ru.pavel2107.arch.admin.domain.user.User;
import ru.pavel2107.arch.admin.service.BrandService;

import java.security.Principal;
import java.util.List;

@RestController
public class BrandController {
    static final Logger logger = LogManager.getLogger(BrandController.class);

    private BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping(value = "microservices/v1/admin/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Brand> listBrands() {
        logger.info( "BRANDS. Список брендов");
        List<Brand> list = brandService.findAll();
        return list;
    }

    @DeleteMapping(value = "microservices/v1/admin/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestParam(value = "id") Long id) {
        logger.info( "BRANDS. DELETE: {}", id);
        brandService.delete(id);
    }

    @PostMapping(value = "microservices/v1/admin/brands", produces = MediaType.APPLICATION_JSON_VALUE)
    public Brand save(@RequestBody Brand brand) {
        logger.info( "BRANDS. SAVE: {}", brand.getCode());
        Brand b = brandService.save(brand);
        return b;
    }
}

