package ru.practicum.shareit.error.exceptions;

import lombok.Getter;

@Getter
public class AvailableItemNotFoundException extends RuntimeException {
    private final Long id;

    public AvailableItemNotFoundException(Long id) {
        this.id = id;
    }

}