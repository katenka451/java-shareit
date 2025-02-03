package ru.practicum.shareit.error.exceptions;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {
    private final Long id;

    public BookingNotFoundException(Long id) {
        this.id = id;
    }

}