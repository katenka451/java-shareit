package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DetailedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.*;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public DetailedBookingDto createBooking(BookingDto newBooking, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(bookerId));
        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(newBooking.getItemId()));

        if (!item.getAvailable()) {
            throw new AvailableItemNotFoundException(newBooking.getItemId());
        }

        log.info("Вызвана операция бронирования вещи {}", item.getId());
        if (newBooking.getStart() == null) {
            throw new ValidationException("Поле 'start' должно быть заполнено");
        }
        if (newBooking.getEnd() == null) {
            throw new ValidationException("Поле 'end' должно быть заполнено");
        }

        newBooking.setStatus(BookingStatus.WAITING);
        newBooking.setBookerId(bookerId);

        return BookingMapper.mapToDetailedBookingDto(
                bookingRepository.save(BookingMapper.mapToBooking(newBooking)),
                booker,
                item);
    }

    public DetailedBookingDto processBooking(Long ownerId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Статус бронирования должен быть заполнен");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        User booker = userRepository.findById(booking.getBooker().getId())
                .orElseThrow(() -> new UserNotFoundException(booking.getBooker().getId()));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException(booking.getItem().getId()));

        if (!item.getAvailable()) {
            throw new AvailableItemNotFoundException(item.getId());
        }

        log.info("Вызвана операция обработки бронирования {}", bookingId);
        if (item.getOwnerId() == null || !item.getOwnerId().equals(ownerId)) {
            throw new ValidationException("Вещь " + item.getId() + " не принадлежит пользователю " + ownerId);
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToDetailedBookingDto(
                bookingRepository.save(booking),
                booker,
                item);
    }

    public DetailedBookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException(booking.getItem().getId()));

        log.info("Вызвана операция получения информации о бронировании {}", bookingId);

        if (booking.getBooker() == null ||
                (!booking.getBooker().getId().equals(userId) && !item.getOwnerId().equals(userId))) {
            throw new ValidationException("Просмотр бронирования " + bookingId +
                    " запрещен для пользователя " + userId);
        }

        return BookingMapper.mapToDetailedBookingDto(booking, user, item);
    }

    public List<DetailedBookingDto> getUserBookings(Long bookerId, BookingState state) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(bookerId));

        switch (state) {
            case ALL -> {
                return bookingRepository.findByBookerId(bookerId)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            case CURRENT -> {
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                                bookerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            case PAST -> {
                return bookingRepository.findByBookerIdAndEndIsBefore(
                                bookerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            case FUTURE -> {
                return bookingRepository.findByBookerIdAndStartIsAfter(
                                bookerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            case WAITING -> {
                return bookingRepository.findByBookerIdAndStatus(
                                bookerId,
                                BookingStatus.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            case REJECTED -> {
                return bookingRepository.findByBookerIdAndStatus(
                                bookerId,
                                BookingStatus.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booker,
                                booking.getItem()))
                        .toList();
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    public List<DetailedBookingDto> getUserItemsBookings(Long ownerId, BookingState state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        switch (state) {
            case ALL -> {
                return bookingRepository.findByItemOwnerId(
                                ownerId)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            case CURRENT -> {
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                                ownerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            case PAST -> {
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(
                                ownerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            case FUTURE -> {
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(
                                ownerId,
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            case WAITING -> {
                return bookingRepository.findByItemOwnerIdAndStatus(
                                ownerId,
                                BookingStatus.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            case REJECTED -> {
                return bookingRepository.findByItemOwnerIdAndStatus(
                                ownerId,
                                BookingStatus.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.mapToDetailedBookingDto(
                                booking,
                                booking.getBooker(),
                                booking.getItem()))
                        .toList();
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

}
