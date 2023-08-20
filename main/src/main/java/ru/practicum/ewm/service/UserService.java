package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.UserConverter;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        var created = UserConverter.convertToModel(userDto);
        var after = userRepository.save(created);
        return UserConverter.convertToDto(after);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        List<User> result = ids != null
                ? userRepository.findAllByIdIn(ids, pageRequest).getContent()
                : userRepository.findAll(pageRequest).getContent();

        return result.size() == 0 ? Collections.emptyList() : UserConverter.mapToDto(result);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}