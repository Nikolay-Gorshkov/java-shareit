package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();

    @Override public Item save(Item item) { storage.put(item.getId(), item); return item; }
    @Override public Optional<Item> findById(Long id) { return Optional.ofNullable(storage.get(id)); }
    @Override public List<Item> findAll() { return new ArrayList<>(storage.values()); }
    @Override public List<Item> findAllByOwner(Long ownerId) {
        return storage.values().stream()
                .filter(i -> Objects.equals(i.getOwner(), ownerId))
                .collect(Collectors.toList());
    }
}
