package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ShareItGatewayApp;
import ru.practicum.client.BookingClient;
import ru.practicum.booking.BookingController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ShareItGatewayApp.class)
@WebMvcTest(BookingController.class)
class BookingControllerGWTest {

    @Autowired MockMvc mvc;
    @MockBean BookingClient client;

    @Test
    void create_badDates_400() throws Exception {
        mvc.perform(post("/bookings").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"start\":\"2025-01-01T12:00:00\",\"end\":\"2025-01-01T11:00:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ok() throws Exception {
        Mockito.when(client.create(Mockito.eq(1L), Mockito.any()))
                .thenReturn(ResponseEntity.ok().body("{}"));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-01T11:00:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void approve_proxy_ok() throws Exception {
        Mockito.when(client.approve(Mockito.eq(2L), Mockito.eq(5L), Mockito.eq(true)))
                .thenReturn(ResponseEntity.ok("{\"id\":5,\"status\":\"APPROVED\"}"));

        mvc.perform(patch("/bookings/5").header("X-Sharer-User-Id","2")
                        .param("approved","true"))
                .andExpect(status().isOk());
    }

    @Test
    void get_and_lists_proxy_ok() throws Exception {
        Mockito.when(client.get(Mockito.eq(1L), Mockito.eq(7L)))
                .thenReturn(ResponseEntity.ok("{\"id\":7}"));
        Mockito.when(client.getUser(Mockito.eq("ALL"), Mockito.eq(1L)))
                .thenReturn(ResponseEntity.ok("[]"));
        Mockito.when(client.getOwner(Mockito.eq("ALL"), Mockito.eq(2L)))
                .thenReturn(ResponseEntity.ok("[]"));

        mvc.perform(get("/bookings/7").header("X-Sharer-User-Id","1"))
                .andExpect(status().isOk());
        mvc.perform(get("/bookings").header("X-Sharer-User-Id","1"))
                .andExpect(status().isOk());
        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id","2"))
                .andExpect(status().isOk());
    }

}
