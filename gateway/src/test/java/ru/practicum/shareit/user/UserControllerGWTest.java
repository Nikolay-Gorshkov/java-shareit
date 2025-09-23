package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.client.UserClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerGWTest {
    @Autowired MockMvc mvc;
    @MockBean UserClient client;

    @Test
    void create_patch_validation() throws Exception {
        Mockito.when(client.create(Mockito.any())).thenReturn(ResponseEntity.ok("{}"));
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"n\",\"email\":\"a@b\"}"))
                .andExpect(status().isOk());

        mvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_getAll() throws Exception {
        Mockito.when(client.get(1L)).thenReturn(ResponseEntity.ok("{}"));
        Mockito.when(client.getAll()).thenReturn(ResponseEntity.ok("[]"));
        mvc.perform(get("/users/1")).andExpect(status().isOk());
        mvc.perform(get("/users")).andExpect(status().isOk());
    }
}