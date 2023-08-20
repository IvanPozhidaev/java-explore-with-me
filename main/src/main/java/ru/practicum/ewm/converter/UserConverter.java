package ru.practicum.ewm.converter;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    public static User convertToModel(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getEmail(),
                dto.getName()
        );
    }

    public static UserDto convertToDto(User model) {
        return new UserDto(
                model.getId(),
                model.getEmail(),
                model.getName()
        );
    }

    public static List<UserDto> mapToDto(List<User> users) {
        return users.stream().map(UserConverter::convertToDto).collect(Collectors.toList());
    }
}