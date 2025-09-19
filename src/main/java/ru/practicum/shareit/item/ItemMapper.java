package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toDto(Item i) {
        if (i == null) return null;
        return ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .requestId(i.getRequest())
                .build();
    }

    public static Item fromDto(ItemDto dto, User owner) {
        if (dto == null) return null;
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(dto.getRequestId())
                .build();
    }

    public static ItemDtoWithBookings toDtoWithBookingsSkeleton(Item i) {
        if (i == null) return null;
        return ItemDtoWithBookings.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .requestId(i.getRequest())
                .build();
    }
}
