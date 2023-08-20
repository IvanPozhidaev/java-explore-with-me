package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("[POST /admin/users] (Admin). Added new user (dto): {}.", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("[GET /admin/users?ids={ids}&from={from}&size={size}] (Admin). " +
                "Get users with param ids: {}, from: {}, size: {}.", ids, from, size);
        return userService.getUsers(ids, from, size);
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("[DELETE /admin/users/{userId}] (Admin). Delete user (id): {}", userId);
        userService.deleteUser(userId);
    }
}