package ru.pavel2107.arch.catalog.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pavel2107.arch.catalog.domain.Category;
import ru.pavel2107.arch.catalog.service.CategoryService;

import java.util.List;

@RestController
public class CategoriesController {
    static final Logger logger = LogManager.getLogger(CategoriesController.class);

    private CategoryService categoryService;

    @Autowired
    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/microservices/v2/catalog/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getCategories() {
        logger.info( "CATEGORIES. Получаем список категорий");
        List<Category> list = categoryService.findAll();
        return list;
    }
}
