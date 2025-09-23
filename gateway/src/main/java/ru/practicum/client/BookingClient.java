package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, Object dto) {
        return post("/bookings", userId, dto, Object.class);
    }

    public ResponseEntity<Object> approve(Long ownerId, Long bookingId, boolean approved) {
        return patch("/bookings/" + bookingId + "?approved=" + approved, ownerId, null, Object.class);
    }

    public ResponseEntity<Object> get(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, null, userId, Object.class);
    }

    public ResponseEntity<Object> getUser(String state, Long userId) {
        return get("/bookings", Map.of("state", state), userId, Object.class);
    }

    public ResponseEntity<Object> getOwner(String state, Long ownerId) {
        return get("/bookings/owner", Map.of("state", state), ownerId, Object.class);
    }
}
