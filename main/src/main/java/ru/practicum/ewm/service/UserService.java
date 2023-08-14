package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.converter.UserConverter;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.util.PageHelper;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto addUser(UserDto userDto) {
        var created = UserConverter.convertToModel(userDto);
        var after = userRepository.save(created);
        return UserConverter.convertToDto(after);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        if (ids != null) {
            var result = userRepository.findAllByIdIn(ids, pageRequest).getContent();
            return result.size() == 0 ? List.of() : UserConverter.mapToDto(result);
        }

        var result = userRepository.findAll(pageRequest).getContent();
        return result.size() == 0 ? List.of() : UserConverter.mapToDto(result);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}