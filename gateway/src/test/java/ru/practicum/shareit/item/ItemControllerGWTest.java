package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ShareItGatewayApp;
import ru.practicum.client.ItemClient;
import ru.practicum.item.ItemController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ShareItGatewayApp.class)
@WebMvcTest(ItemController.class)
class ItemControllerGWTest {
    @Autowired MockMvc mvc;
    @MockBean ItemClient client;

    @Test
    void add_ok_and_search_blank() throws Exception {
        Mockito.when(client.add(Mockito.eq(1L), Mockito.any())).thenReturn(ResponseEntity.ok("{}"));
        mvc.perform(post("/items").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"n\",\"description\":\"d\",\"available\":true}"))
                .andExpect(status().isOk());

        mvc.perform(get("/items/search?text=")).andExpect(status().isOk());
    }

    @Test
    void patch_requestIdValidation() throws Exception {
        mvc.perform(patch("/items/1").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestId\":0}"))
                .andExpect(status().isBadRequest());
    }
}
