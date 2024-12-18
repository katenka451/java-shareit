package ru.practicum.shareit.error.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final Long id;

    public UserNotFoundException(Long id) {
        this.id = id;
    }

}