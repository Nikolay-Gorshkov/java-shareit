package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();

    @Override public User save(User user) { storage.put(user.getId(), user); return user; }
    @Override public Optional<User> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
    @Override public List<User> findAll() { return new ArrayList<>(storage.values()); }
    @Override public void deleteById(Long id) { storage.remove(id); }
    @Override public boolean existsEmail(String email, Long ignoreUserId) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)
                        && (ignoreUserId == null || !u.getId().equals(ignoreUserId)));
    }
}