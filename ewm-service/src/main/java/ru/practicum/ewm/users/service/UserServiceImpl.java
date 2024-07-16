package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(Long[] ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids == null) {
            return userRepository.findAll(pageRequest).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        return userRepository.findUsersByIds(ids, pageRequest).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long userId) {
        checkIfUserExists(userId);

        userRepository.deleteById(userId);
    }

    @Override
    public void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
    }
}
