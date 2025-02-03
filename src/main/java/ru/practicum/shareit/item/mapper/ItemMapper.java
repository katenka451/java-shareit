package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookDateDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : false)
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemWithBookDateDto mapToItemWithBookDateDto(Item item,
                                                               Booking lastBooking,
                                                               Booking nextBooking) {
        return ItemWithBookDateDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : false)
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .lastBooking(lastBooking != null ? BookingMapper.mapToBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? BookingMapper.mapToBookingDto(nextBooking) : null)
                .build();
    }

    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : false)
                .ownerId(itemDto.getOwnerId())
                .requestId(itemDto.getRequestId())
                .build();
    }

}
