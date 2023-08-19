package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("[GET /categories?from={from}&size={size}] (Public). " +
                "Get categories with param from: {}, size: {}.", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("[GET /categories/{catId}] (Public). Get category (id): {}", catId);
        return categoryService.getCategoryById(catId);
    }
}