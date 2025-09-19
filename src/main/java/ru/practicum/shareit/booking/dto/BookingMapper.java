package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.ZoneId;

public class BookingMapper {
    public static BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .item(ItemShortDto.builder()
                        .id(b.getItem().getId())
                        .name(b.getItem().getName())
                        .build())
                .booker(UserShortDto.builder()
                        .id(b.getBooker().getId())
                        .name(b.getBooker().getName())
                        .build())
                .start(b.getStart() == null ? null
                        : b.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .end(b.getEnd() == null ? null
                        : b.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .status(b.getStatus().name())
                .build();
    }

    public static Booking fromCreateDto(BookingCreateDto dto, Item item, User booker) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(dto.getStart().atZone(ZoneId.systemDefault()).toInstant())
                .end(dto.getEnd().atZone(ZoneId.systemDefault()).toInstant())
                .status(Booking.Status.WAITING)
                .build();
    }
}
