package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookDateDto;

import java.util.List;

public interface ItemService {

    List<ItemWithBookDateDto> getAllUserItems(Long userId);

    ItemWithBookDateDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItems(String text, Long userId);

    ItemDto createItem(ItemDto newItem, Long userId);

    ItemDto updateItem(Long itemId, ItemDto updItem, Long userId);

    CommentDto addComment(Long authorId, Long itemId, CommentDto newComment);

}
