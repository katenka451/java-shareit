package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllUserItems(Long userId);
    ItemDto getItemById(Long itemId);
    List<ItemDto> searchItems(String text, Long userId);
    ItemDto createItem(ItemDto newItem, Long userId);
    ItemDto updateItem(Long itemId, ItemDto updItem, Long userId);

}
