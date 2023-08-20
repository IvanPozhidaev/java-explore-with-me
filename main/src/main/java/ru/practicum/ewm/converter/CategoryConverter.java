package ru.practicum.ewm.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryConverter {

    public Category convertToModel(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getName()
        );
    }

    public CategoryDto convertToDto(Category model) {
        return new CategoryDto(
                model.getId(),
                model.getName()
        );
    }

    public List<CategoryDto> mapToDto(List<Category> cats) {
        return cats.stream().map(CategoryConverter::convertToDto).collect(Collectors.toList());

    }
}