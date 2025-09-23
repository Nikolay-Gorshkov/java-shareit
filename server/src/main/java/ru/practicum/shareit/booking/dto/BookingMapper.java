package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.ZoneOffset;

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
                // Храним в БД Instant (UTC), наружу отдаём LocalDateTime в UTC
                .start(b.getStart() == null ? null
                        : b.getStart().atZone(ZoneOffset.UTC).toLocalDateTime())
                .end(b.getEnd() == null ? null
                        : b.getEnd().atZone(ZoneOffset.UTC).toLocalDateTime())
                .status(b.getStatus().name())
                .build();
    }

    public static Booking fromCreateDto(BookingCreateDto dto, Item item, User booker) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                // Принимаем LocalDateTime от клиента как время в UTC и сохраняем как Instant
                .start(dto.getStart().atZone(ZoneOffset.UTC).toInstant())
                .end(dto.getEnd().atZone(ZoneOffset.UTC).toInstant())
                .status(Booking.Status.WAITING)
                .build();
    }
}

