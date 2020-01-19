package ru.pavel2107.arch.catalog.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.catalog.domain.User;
import ru.pavel2107.arch.catalog.dto.GoodDto;
import ru.pavel2107.arch.catalog.service.GoodsService;
import ru.pavel2107.arch.catalog.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
public class CatalogController {

    static final Logger logger = LogManager.getLogger(CatalogController.class);

    private GoodsService goodsService;
    private UserService userService;

    public CatalogController(GoodsService goodsService, UserService userService) {
        this.goodsService = goodsService;
        this.userService = userService;
    }

    @GetMapping(value = "/microservices/v2/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoods(Principal principal) {
        User user = findUser(principal);
        logger.info( "GOODS. USER: {} Список товаров", user.getEmail());
        List<GoodDto> list = goodsService.findAll(user);
        return list;
    }

    @GetMapping(value = "/microservices/v2/catalog/bycategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoodsByCategory(Principal principal, @RequestParam Long categoryId) {
        User user = findUser(principal);
        logger.info( "GOODS. USER: {} CATEGORY: {} Список товаров для категории", user.getEmail(), categoryId);
        List<GoodDto> list = goodsService.findByCategory(user, categoryId);
        return list;
    }

    @GetMapping(value = "/microservices/v2/catalog/bybrand", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GoodDto> listGoodsByBrand(Principal principal, @RequestParam Long brandId) {
        User user = findUser(principal);
        logger.info( "GOODS. USER: {} BRAND: {} Список товаров для бренда", user.getEmail(), brandId);
        List<GoodDto> list = goodsService.findByBrand(user, brandId);
        return list;
    }

    @GetMapping(value = "/microservices/v2/catalog/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GoodDto get(Principal principal, @PathVariable(value = "id") Long id) {
        User user = findUser(principal);
        logger.info( "GOODS. USER: {} GOOD: {} Товар", user.getEmail(), id);
        GoodDto good = goodsService.find(user, id);
        return good;
    }

    private User findUser(Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName());
        }
        ;
        return user;
    }
}
