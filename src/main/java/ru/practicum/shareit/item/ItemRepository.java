package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.*;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findAllByOwner(Long ownerId);
}