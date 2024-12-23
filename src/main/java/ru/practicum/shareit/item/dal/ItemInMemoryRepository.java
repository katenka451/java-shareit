package ru.practicum.shareit.item.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.CreationException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemInMemoryRepository {

    private final HashMap<Long, HashMap<Long, Item>> items = new HashMap<>();
    private long id = 0;

    public List<Item> getAllUserItems(long userId) {
        return items.get(userId).values().stream().toList();
    }

    public Optional<Item> getItemById(long itemId) {
        return items.values()
                .stream()
                .flatMap(i -> i.values().stream())
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    public List<Item> searchItems(String text, Long userId) {
        return items.values()
                .stream()
                .flatMap(i -> i.values().stream())
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    public Item createItem(Item item, long userId) {
        item.setId(++this.id);
        if (!items.containsKey(userId)) {
            items.put(userId, new HashMap<>());
        }
        items.get(userId).put(item.getId(), item);
        if (!items.get(userId).containsKey(item.getId())) {
            throw new CreationException("Ошибка создания вещи " + item.getName());
        }
        return item;
    }

    public Item updateItem(long itemId, Item item, long userId) {
        Item updItem = items.get(userId).get(itemId);
        if (updItem == null) {
            return null;
        }

        if (item.getName() != null &&
                !item.getName().isEmpty() &&
                !item.getName().equals(updItem.getName())) {
            updItem.setName(item.getName());
        }

        if (item.getDescription() != null &&
                !item.getDescription().isEmpty() &&
                !item.getDescription().equals(updItem.getDescription())) {
            updItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null &&
                !item.getAvailable().equals(updItem.getAvailable())) {
            updItem.setAvailable(item.getAvailable());
        }

        items.get(userId).put(itemId, updItem);
        return updItem;
    }

    public boolean isUserHasItem(Long userId, Long itemId) {
        if (!items.containsKey(userId)) {
            return false;
        }
        return items.get(userId).containsKey(itemId);
    }
}
