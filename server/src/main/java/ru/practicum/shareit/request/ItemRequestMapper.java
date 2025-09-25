package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest e) {
        return ItemRequestDto.builder()
                .id(e.getId())
                .description(e.getDescription())
                .requestorId(e.getRequestor().getId())
                .created(e.getCreated())
                .build();
    }

    public static ItemRequestOutDto toOutDto(ItemRequest e, List<Item> items) {
        return ItemRequestOutDto.builder()
                .id(e.getId())
                .description(e.getDescription())
                .requestorId(e.getRequestor().getId())
                .created(e.getCreated())
                .items(items.stream().map(it ->
                        ItemRequestOutDto.ItemReply.builder()
                                .id(it.getId())
                                .name(it.getName())
                                .ownerId(it.getOwner().getId())
                                .build()
                ).toList())
                .build();
    }

    public static ItemRequest fromCreateDto(ItemRequestDto dto, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }
}