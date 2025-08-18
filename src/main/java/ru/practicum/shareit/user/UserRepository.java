package ru.practicum.shareit.user;

import java.util.*;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsEmail(String email, Long ignoreUserId);
}