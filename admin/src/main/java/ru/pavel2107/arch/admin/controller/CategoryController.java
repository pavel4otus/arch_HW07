package ru.pavel2107.arch.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.pavel2107.arch.admin.domain.catalog.Brand;
import ru.pavel2107.arch.admin.domain.catalog.Category;
import ru.pavel2107.arch.admin.service.BrandService;
import ru.pavel2107.arch.admin.service.CategoryService;
import ru.pavel2107.arch.admin.service.CategoryServiceImpl;

import java.util.List;

@RestController
public class CategoryController {

    static final Logger logger = LogManager.getLogger(CategoryController.class);

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "microservices/v1/admin/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> listcategories(Authentication auth) {
        logger.info( "CATEGORIES. список категорий");
        List<Category> list = categoryService.findAll();
        return list;
    }

    @DeleteMapping(value = "microservices/v1/admin/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestParam(value = "id") Long id) {
        logger.info( "CATEGORIES. DELETE: {}", id);
        categoryService.delete(id);
    }

    @PostMapping(value = "microservices/v1/admin/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category save(@RequestBody Category category) {
        logger.info( "CATEGORIES. SAVE: {}", category.getCode());
        return categoryService.save(category);
    }
}

