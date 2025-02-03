package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.DetailedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static DetailedBookingDto mapToDetailedBookingDto(Booking booking, User user, Item item) {
        return DetailedBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(item))
                .booker(UserMapper.mapToUserDto(user))
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(Item.builder().id(bookingDto.getItemId()).build())
                .booker(User.builder().id(bookingDto.getBookerId()).build())
                .status(bookingDto.getStatus())
                .build();
    }

    public static Booking mapFromDetailedToBooking(DetailedBookingDto detailedBookingDto) {
        return Booking.builder()
                .id(detailedBookingDto.getId())
                .start(detailedBookingDto.getStart())
                .end(detailedBookingDto.getEnd())
                .item(ItemMapper.mapToItem(detailedBookingDto.getItem()))
                .booker(UserMapper.mapToUser(detailedBookingDto.getBooker()))
                .status(detailedBookingDto.getStatus())
                .build();
    }

}
