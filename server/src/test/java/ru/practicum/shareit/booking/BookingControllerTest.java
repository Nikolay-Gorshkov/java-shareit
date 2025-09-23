package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BookingService service;

    @Test
    void create_ok() throws Exception {
        Mockito.when(service.create(Mockito.eq(1L), Mockito.any()))
                .thenReturn(BookingDto.builder().id(10L).status("WAITING").build());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":5,\"start\":\"2025-01-01T11:00:00\",\"end\":\"2025-01-01T12:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void get_getUser_getOwner() throws Exception {
        Mockito.when(service.get(Mockito.eq(1L), Mockito.eq(5L)))
                .thenReturn(BookingDto.builder().id(5L).build());
        Mockito.when(service.getUserBookings(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn(List.of());
        Mockito.when(service.getOwnerBookings(Mockito.eq(2L), Mockito.anyString()))
                .thenReturn(List.of());

        mvc.perform(get("/bookings/5").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void approve_ok() throws Exception {
        Mockito.when(service.approve(Mockito.eq(2L), Mockito.eq(5L), Mockito.eq(true)))
                .thenReturn(BookingDto.builder().id(5L).status("APPROVED").build());

        mvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", "2")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

}
