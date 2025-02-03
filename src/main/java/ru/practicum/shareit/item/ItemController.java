package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookDateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemWithBookDateDto> getAllUserItems(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookDateDto getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                           @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_ID_HEADER) long userId,
                                     @RequestParam(name = "text") String text) {
        return itemService.searchItems(text, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
                          @Valid @RequestBody ItemDto newItem) {
        return itemService.createItem(newItem, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long authorId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto newComment) {
        return itemService.addComment(authorId, itemId, newComment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable Long itemId,
                          @Valid @RequestBody ItemDto updItem) {
        return itemService.updateItem(itemId, updItem, userId);
    }

}
