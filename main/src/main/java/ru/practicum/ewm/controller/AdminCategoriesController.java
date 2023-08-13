package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        var addedCat = categoryService.addCategory(categoryDto);
        log.info("[POST /admin/categories] (Admin). Added new category (dto): {}.", categoryDto);
        return addedCat;
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        var updatedCat = categoryService.updateCategory(categoryDto, catId);
        log.info("[PATCH /admin/categories/{catId}] (Admin). Category (id) {} update to (dto): {}", catId, categoryDto);
        return updatedCat;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
        log.info("[DELETE /admin/categories{catId}] (Admin). Delete category (id): {}", catId);
    }
}