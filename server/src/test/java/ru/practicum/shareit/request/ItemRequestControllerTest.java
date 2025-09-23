package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ItemRequestService service;

    @Test
    void create_and_gets() throws Exception {
        Mockito.when(service.create(Mockito.eq(1L), Mockito.any()))
                .thenReturn(ItemRequestDto.builder().id(10L).description("need").build());

        mvc.perform(post("/requests").header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"need\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10));

        Mockito.when(service.getOwn(1L)).thenReturn(List.of());
        mvc.perform(get("/requests").header("X-Sharer-User-Id", "1")).andExpect(status().isOk());

        Mockito.when(service.getAll(1L, 0, 10)).thenReturn(List.of(
                ItemRequestOutDto.builder().id(5L).build()));
        mvc.perform(get("/requests/all?from=0&size=10").header("X-Sharer-User-Id","1"))
                .andExpect(status().isOk());

        Mockito.when(service.getById(1L, 5L)).thenReturn(ItemRequestOutDto.builder().id(5L).build());
        mvc.perform(get("/requests/5").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(5));
    }
}
