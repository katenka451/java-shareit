package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DetailedBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public DetailedBookingDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<DetailedBookingDto> getAllUserBookings(@RequestHeader(USER_ID_HEADER) long bookerId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<DetailedBookingDto> getAllUserItemsBookings(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                            @RequestParam(name = "state") BookingState state) {
        return bookingService.getUserItemsBookings(ownerId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetailedBookingDto create(@RequestHeader(USER_ID_HEADER) long bookerId,
                                     @Valid @RequestBody BookingDto newBooking) {
        return bookingService.createBooking(newBooking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public DetailedBookingDto processBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                             @PathVariable long bookingId,
                                             @RequestParam(name = "approved") boolean approved) {
        return bookingService.processBooking(ownerId, bookingId, approved);
    }
}
