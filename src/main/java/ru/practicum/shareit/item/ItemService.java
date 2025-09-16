package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto add(Long userId, ItemDto dto);

    ItemDto update(Long userId, Long itemId, ItemDto patch);

    ItemDtoWithBookings get(Long itemId, Long requesterId);

    List<ItemDtoWithBookings> getOwnerItems(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto dto);
}