package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DetailedBookingDto;

import java.util.List;

public interface BookingService {

    DetailedBookingDto createBooking(BookingDto bookingDto, Long bookerId);

    DetailedBookingDto processBooking(Long ownerId, Long bookingId, Boolean approved);

    DetailedBookingDto getBookingById(Long userId, Long bookingId);

    List<DetailedBookingDto> getUserBookings(Long bookerId, BookingState state);

    List<DetailedBookingDto> getUserItemsBookings(Long ownerId, BookingState state);

}
