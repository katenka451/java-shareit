package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exceptions.BookingNotFoundException;
import ru.practicum.shareit.error.exceptions.ItemNotFoundException;
import ru.practicum.shareit.error.exceptions.UserNotFoundException;
import ru.practicum.shareit.error.exceptions.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookDateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public List<ItemWithBookDateDto> getAllUserItems(Long userId) {
        List<ItemWithBookDateDto> itemWithBookDateDto = new ArrayList<>();
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Booking> lastBookings = bookingRepository
                .findFirstByItemIdInAndStartIsBeforeOrderByStartDesc(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getItem().getId(),
                        entry -> entry
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findFirstByItemIdInAndStartIsAfterOrderByStart(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getItem().getId(),
                        entry -> entry
                ));

        for (Item item : items) {
            itemWithBookDateDto.add(ItemMapper.mapToItemWithBookDateDto(
                    item,
                    lastBookings.get(item.getId()),
                    nextBookings.get(item.getId())));
        }

        return itemWithBookDateDto;
    }

    public ItemWithBookDateDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        Optional<Booking> lastBooking = Optional.empty();
        Optional<Booking> nextNearestBooking = Optional.empty();

        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(item.getOwnerId(), userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByStartDesc(
                    itemId,
                    now);

            nextNearestBooking = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStart(
                    itemId,
                    now);
        }

        ItemWithBookDateDto itemDto = ItemMapper.mapToItemWithBookDateDto(
                item,
                lastBooking.orElse(null),
                nextNearestBooking.orElse(null));

        itemDto.setComments(comments);
        return itemDto;
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    public ItemDto createItem(ItemDto newItem, Long userId) {
        validateDataCreation(newItem, userId);
        newItem.setOwnerId(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItem(newItem)));
    }

    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto newComment) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(authorId, itemId, LocalDateTime.now())) {
            throw new BookingNotFoundException(authorId);
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        newComment.setCreated(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository
                .save(CommentMapper.mapToComment(newComment, item, author)));
    }

    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto updItem, Long userId) {
        validateDataUpdate(itemId, userId);
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (updItem.getName() != null &&
                !updItem.getName().isEmpty() &&
                !updItem.getName().equals(itemToUpdate.getName())) {
            itemToUpdate.setName(updItem.getName());
        }

        if (updItem.getDescription() != null &&
                !updItem.getDescription().isEmpty() &&
                !updItem.getDescription().equals(itemToUpdate.getDescription())) {
            itemToUpdate.setDescription(updItem.getDescription());
        }

        if (updItem.getAvailable() != null &&
                !updItem.getAvailable().equals(itemToUpdate.getAvailable())) {
            itemToUpdate.setAvailable(updItem.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemRepository.save(itemToUpdate));
    }

    private void validateDataCreation(ItemDto item, Long userId) {
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            throw new ValidationException("Наименование должно быть заполнено");
        }

        log.info("Вызвана операция создания вещи {}", item.getName());

        if (userRepository.findById(userId).isEmpty()) {
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

        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        log.info("Вызвана операция обновления вещи {}", itemId);

        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        if (!itemToUpdate.getOwnerId().equals(userId)) {
            throw new ItemNotFoundException(itemId);
        }

    }
}
