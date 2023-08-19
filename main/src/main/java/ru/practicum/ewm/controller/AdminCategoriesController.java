package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("[POST /admin/categories] (Admin). Added new category (dto): {}.", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        log.info("[PATCH /admin/categories/{catId}] (Admin). Category (id) {} update to (dto): {}", catId, categoryDto);
        return categoryService.updateCategory(categoryDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("[DELETE /admin/categories{catId}] (Admin). Delete category (id): {}", catId);
        categoryService.deleteCategory(catId);
    }
}