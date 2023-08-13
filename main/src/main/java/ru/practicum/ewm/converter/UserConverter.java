package ru.practicum.ewm.converter;

import ru.practicum.ewm.model.dto.UserDto;
import ru.practicum.ewm.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserConverter {

    public static UserModel convertToModel(UserDto dto) {
        return new UserModel(
                dto.getId(),
                dto.getEmail(),
                dto.getName()
        );
    }

    public static UserDto convertToDto(UserModel model) {
        return new UserDto(
                model.getId(),
                model.getEmail(),
                model.getName()
        );
    }

    public static List<UserDto> mapToDto(List<UserModel> users) {
        List<UserDto> res = new ArrayList<>();
        for (UserModel u : users) {
            res.add(convertToDto(u));
        }
        return res;
    }
}