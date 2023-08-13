package ru.practicum.ewm.converter;

import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryConverter {

    public static CategoryModel convertToModel(CategoryDto dto) {
        return new CategoryModel(
                dto.getId(),
                dto.getName()
        );
    }

    public static CategoryDto convertToDto(CategoryModel model) {
        return new CategoryDto(
                model.getId(),
                model.getName()
        );
    }

    public static List<CategoryDto> mapToDto(List<CategoryModel> cats) {
        List<CategoryDto> res = new ArrayList<>();
        for (CategoryModel c : cats) {
            res.add(convertToDto(c));
        }
        return res;
    }
}