package ru.practicum.shareit.error.exceptions;

import lombok.Getter;

@Getter
public class ItemNotFoundException extends RuntimeException {
    private final Long id;

    public ItemNotFoundException(Long id) {
        this.id = id;
    }

}