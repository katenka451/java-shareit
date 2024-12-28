package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.DataConflictException;
import ru.practicum.shareit.error.exceptions.UserNotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public UserDto createUser(UserDto newUser) {
        validateDataCreation(newUser);
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(newUser)));
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto updUser) {
        validateDataUpdate(userId, updUser);
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (updUser.getName() != null &&
                !updUser.getName().isEmpty() &&
                !updUser.getName().equals(userToUpdate.getName())) {
            userToUpdate.setName(updUser.getName());
        }

        if (updUser.getEmail() != null &&
                !updUser.getEmail().isEmpty() &&
                !updUser.getEmail().equals(userToUpdate.getEmail())) {
            userToUpdate.setEmail(updUser.getEmail());
        }
        return UserMapper.mapToUserDto(userRepository.save(userToUpdate));
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
        return true;
    }

    private void validateDataCreation(UserDto user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            throw new ValidationException("Имя должно быть заполнено");
        }

        log.info("Вызвана операция создания пользователя {}", user.getName());
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("E-mail должен быть заполнен");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
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

        if (user.getEmail() != null &&
                userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataConflictException("E-mail " + user.getEmail() + " уже используется");
        }
    }

}
