package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.ItemNotFoundException;
import ru.practicum.shareit.error.exceptions.UserNotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.item.dal.ItemInMemoryRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dal.UserInMemoryRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemInMemoryRepository itemRepository;
    private final UserInMemoryRepository userRepository;

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        return itemRepository.getAllUserItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto getItemById(Long itemId) {
        return itemRepository.getItemById(itemId)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text, userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto createItem(ItemDto newItem, Long userId) {
        validateDataCreation(newItem, userId);
        return ItemMapper.mapToItemDto(itemRepository.createItem(ItemMapper.mapToItem(newItem), userId));
    }

    public ItemDto updateItem(Long itemId, ItemDto updItem, Long userId) {
        validateDataUpdate(itemId, userId);
        return ItemMapper.mapToItemDto(itemRepository.updateItem(itemId, ItemMapper.mapToItem(updItem), userId));
    }

    private void validateDataCreation(ItemDto item, Long userId) {
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            throw new ValidationException("Наименование должно быть заполнено");
        }

        log.info("Вызвана операция создания вещи {}", item.getName());

        if (userRepository.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Поле 'available' должно быть заполнено");
        }

        if (item.getDescription().isEmpty() || item.getDescription().isBlank()) {
            throw new ValidationException("Поле 'description' должно быть заполнено");
        }
    }

    private void validateDataUpdate(Long itemId, Long userId) {
        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }

        log.info("Вызвана операция обновления вещи {}", itemId);

        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        if (!itemRepository.isUserHasItem(userId, itemId)) {
            throw new ItemNotFoundException(itemId);
        }

    }
}
