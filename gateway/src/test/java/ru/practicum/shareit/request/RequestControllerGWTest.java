package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ShareItGatewayApp;
import ru.practicum.client.RequestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerGWTest {
    @Autowired MockMvc mvc;
    @MockBean RequestClient client;

    @Test
    void create_gets() throws Exception {
        Mockito.when(client.create(Mockito.eq(1L), Mockito.any())).thenReturn(ResponseEntity.ok("{}"));
        mvc.perform(post("/requests").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"need\"}"))
                .andExpect(status().isOk());

        Mockito.when(client.getOwn(1L)).thenReturn(ResponseEntity.ok("[]"));
        mvc.perform(get("/requests").header("X-Sharer-User-Id","1")).andExpect(status().isOk());

        Mockito.when(client.getAll(1L, 0, 10)).thenReturn(ResponseEntity.ok("[]"));
        mvc.perform(get("/requests/all?from=0&size=10").header("X-Sharer-User-Id","1")).andExpect(status().isOk());

        Mockito.when(client.getById(1L, 5L)).thenReturn(ResponseEntity.ok("{}"));
        mvc.perform(get("/requests/5").header("X-Sharer-User-Id","1")).andExpect(status().isOk());
    }
}