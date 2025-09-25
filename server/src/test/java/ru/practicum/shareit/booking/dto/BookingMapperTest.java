package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toDto_fromCreateDto() {
        var item = Item.builder().id(10L).name("n").build();
        var booker = User.builder().id(1L).name("u").build();

        var dtoIn = BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2025,1,1,11,0))
                .end(LocalDateTime.of(2025,1,1,12,0))
                .build();

        var entity = BookingMapper.fromCreateDto(dtoIn, item, booker);
        assertThat(entity.getStart())
                .isEqualTo(dtoIn.getStart().atZone(ZoneOffset.UTC).toInstant());

        var back = BookingMapper.toDto(Booking.builder()
                .id(5L).item(item).booker(booker)
                .status(Booking.Status.WAITING)
                .start(Instant.parse("2025-01-01T11:00:00Z"))
                .end(Instant.parse("2025-01-01T12:00:00Z")).build());

        assertThat(back.getItem().getName()).isEqualTo("n");
        assertThat(back.getBooker().getId()).isEqualTo(1L);
    }
}

