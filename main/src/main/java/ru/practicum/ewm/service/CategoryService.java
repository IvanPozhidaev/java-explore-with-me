package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.CategoryConverter;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.util.PageHelper;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        var created = CategoryConverter.convertToModel(categoryDto);
        var after = categoryRepository.save(created);
        return CategoryConverter.convertToDto(after);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        var updatedCat = getIfExistCategoryById(catId);
        updatedCat.setName(categoryDto.getName());
        var afterUpdate = categoryRepository.save(updatedCat);
        return CategoryConverter.convertToDto(afterUpdate);
    }

    public void deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);
    }

    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var result = categoryRepository.findAll(pageRequest).getContent();
        return result.size() == 0 ? Collections.emptyList() : CategoryConverter.mapToDto(result);
    }

    public CategoryDto getCategoryById(Long catId) {
        var result = getIfExistCategoryById(catId);
        return CategoryConverter.convertToDto(result);
    }

    private Category getIfExistCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new MainNotFoundException(String.format("Category with id=%s was not found", catId)));
    }
}