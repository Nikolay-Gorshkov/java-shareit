package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired MockMvc mvc;
    @MockBean ItemService service;

    @Test
    void add_patch_get_search() throws Exception {
        Mockito.when(service.add(Mockito.eq(1L), Mockito.any())).thenReturn(ItemDto.builder().id(10L).build());
        mvc.perform(post("/items").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"n\",\"description\":\"d\",\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        Mockito.when(service.update(Mockito.eq(1L), Mockito.eq(10L), Mockito.any()))
                .thenReturn(ItemDto.builder().id(10L).name("n2").build());
        mvc.perform(patch("/items/10").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"n2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("n2"));

        Mockito.when(service.get(Mockito.eq(10L), Mockito.eq(1L)))
                .thenReturn(ItemDtoWithBookings.builder().id(10L).build());
        mvc.perform(get("/items/10").header("X-Sharer-User-Id","1"))
                .andExpect(status().isOk());

        Mockito.when(service.getOwnerItems(1L)).thenReturn(List.of());
        mvc.perform(get("/items").header("X-Sharer-User-Id","1")).andExpect(status().isOk());

        Mockito.when(service.search("q")).thenReturn(List.of());
        mvc.perform(get("/items/search?text=q")).andExpect(status().isOk());
    }

    @Test
    void addComment_ok() throws Exception {
        Mockito.when(service.addComment(Mockito.eq(1L), Mockito.eq(10L), Mockito.any()))
                .thenReturn(ru.practicum.shareit.item.dto.CommentDto.builder()
                        .id(100L).text("great!").authorName("u").build());

        mvc.perform(post("/items/10/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"great!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("great!"));
    }

    @Test
    void addComment_badRequest_whenEmptyText() throws Exception {
        Mockito.when(service.addComment(Mockito.eq(1L), Mockito.eq(10L), Mockito.any()))
                .thenThrow(new ru.practicum.shareit.exception.ValidationException("Текст комментария обязателен"));

        mvc.perform(post("/items/10/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

}
