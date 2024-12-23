package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.DataConflictException;
import ru.practicum.shareit.error.exceptions.UserNotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.user.dal.UserInMemoryRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserInMemoryRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserDto createUser(UserDto newUser) {
        validateDataCreation(newUser);
        return UserMapper.mapToUserDto(userRepository.createUser(UserMapper.mapToUser(newUser)));
    }

    public UserDto updateUser(Long userId, UserDto updUser) {
        validateDataUpdate(userId, updUser);
        return UserMapper.mapToUserDto(userRepository.updateUser(userId, UserMapper.mapToUser(updUser)));
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteUser(userId);
        return true;
    }

    private void validateDataCreation(UserDto user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            throw new ValidationException("Имя должно быть заполнено");
        }

        log.info("Вызвана операция создания пользователя {}", user.getName());
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("E-mail должен быть заполнен");
        }
        if (userRepository.isEmailUsed(user.getEmail())) {
            throw new DataConflictException("E-mail " + user.getEmail() + " уже используется");
        }
    }

    private void validateDataUpdate(Long userId, UserDto user) {
        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        log.info("Вызвана операция обновления пользователя {}", userId);
        if (getUserById(userId) == null) {
            throw new UserNotFoundException(userId);
        }

        if (userRepository.isEmailUsed(user.getEmail())) {
            throw new DataConflictException("E-mail " + user.getEmail() + " уже используется");
        }
    }

}
