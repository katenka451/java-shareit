package ru.practicum.shareit.user.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.CreationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserInMemoryRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User createUser(User user) {
        user.setId(++this.id);
        users.put(user.getId(), user);
        if (!users.containsKey(user.getId())) {
            throw new CreationException("Ошибка создания пользователя " + user.getName());
        }
        return user;
    }

    public User updateUser(long id, User user) {
        User updUser = users.get(id);
        if (updUser == null) {
            return null;
        }

        if (user.getName() != null &&
                !user.getName().isEmpty() &&
                !user.getName().equals(updUser.getName())) {
            updUser.setName(user.getName());
        }

        if (user.getEmail() != null &&
                !user.getEmail().isEmpty() &&
                !user.getEmail().equals(updUser.getEmail())) {
            updUser.setEmail(user.getEmail());
        }

        users.put(id, updUser);
        return updUser;
    }

    public void deleteUser(long id) {
        if (users.isEmpty()) {
            return;
        }
        users.remove(id);
    }

    public boolean isEmailUsed(String email) {
        if (users.isEmpty()) {
            return false;
        }
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
