package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutDtoJsonTest {

    @Autowired JacksonTester<ItemRequestOutDto> json;

    @Test
    void serialize() throws Exception {
        var dto = ItemRequestOutDto.builder()
                .id(5L).description("need").requestorId(1L)
                .created(LocalDateTime.parse("2025-01-01T10:00:00"))
                .items(List.of(
                        ItemRequestOutDto.ItemReply.builder().id(100L).name("drill").ownerId(2L).build()
                ))
                .build();

        var c = json.write(dto);
        assertThat(c).extractingJsonPathStringValue("$.description").isEqualTo("need");
        assertThat(c).extractingJsonPathArrayValue("$.items").hasSize(1);
    }
}
