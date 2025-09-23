package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired JacksonTester<BookingDto> json;

    @Test
    void serializeDeserialize() throws Exception {
        var dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2025-01-01T10:00:00"))
                .end(LocalDateTime.parse("2025-01-01T11:00:00"))
                .status("WAITING")
                .build();

        var content = json.write(dto);
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        var back = json.parseObject(content.getJson());
        assertThat(back.getId()).isEqualTo(1L);
        assertThat(back.getStart()).isEqualTo(LocalDateTime.parse("2025-01-01T10:00:00"));
    }
}
