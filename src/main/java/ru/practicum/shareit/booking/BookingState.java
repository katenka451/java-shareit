package ru.practicum.shareit.booking;

public enum BookingState {
    /**
     * Все
     */
    ALL,
    /**
     * Текущие
     */
    CURRENT,
    /**
     * Завершенные
     */
    PAST,
    /**
     * Будущие
     */
    FUTURE,
    /**
     * Ожидающие подтверждения
     */
    WAITING,
    /**
     * Отклоненные
     */
    REJECTED
}
