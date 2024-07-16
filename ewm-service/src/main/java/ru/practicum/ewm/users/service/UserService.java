package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Long[] ids, Integer from, Integer size);

    UserDto create(UserDto userDto);

    void deleteUserById(Long userId);

    void checkIfUserExists(Long userId);
}
