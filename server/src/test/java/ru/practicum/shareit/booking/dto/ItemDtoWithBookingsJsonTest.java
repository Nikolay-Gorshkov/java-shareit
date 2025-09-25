package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingsJsonTest {

    @Autowired JacksonTester<ItemDtoWithBookings> json;

    @Test
    void serialize() throws Exception {
        var dto = ItemDtoWithBookings.builder()
                .id(1L).name("n").description("d").available(true).requestId(9L)
                .lastBooking(new ItemDtoWithBookings.BookingShort(10L, 20L))
                .nextBooking(new ItemDtoWithBookings.BookingShort(11L, 21L))
                .comments(List.of())
                .build();
        var c = json.write(dto);
        assertThat(c).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(c).extractingJsonPathNumberValue("$.requestId").isEqualTo(9);
    }
}