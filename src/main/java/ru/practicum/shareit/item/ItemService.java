package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto add(Long userId, ItemDto dto);
    ItemDto update(Long userId, Long itemId, ItemDto patch);
    ItemDto get(Long itemId, Long requesterId);
    List<ItemDto> getOwnerItems(Long ownerId);
    List<ItemDto> search(String text);
}