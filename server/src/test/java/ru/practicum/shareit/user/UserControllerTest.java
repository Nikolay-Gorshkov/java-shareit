package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @MockBean UserService service;

    @Test
    void crud() throws Exception {
        Mockito.when(service.create(Mockito.any())).thenReturn(UserDto.builder().id(1L).build());
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"n\",\"email\":\"a@b\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));

        Mockito.when(service.update(Mockito.eq(1L), Mockito.any())).thenReturn(UserDto.builder().id(1L).name("x").build());
        mvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"x\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("x"));

        Mockito.when(service.get(1L)).thenReturn(UserDto.builder().id(1L).build());
        mvc.perform(get("/users/1")).andExpect(status().isOk());

        Mockito.when(service.getAll()).thenReturn(List.of());
        mvc.perform(get("/users")).andExpect(status().isOk());

        mvc.perform(delete("/users/1")).andExpect(status().isOk());
    }
}
