package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.booking.dto.BookingCreateIn;

import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private static final String HEADER_USER = "X-Sharer-User-Id";
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER) Long userId,
                                         @RequestBody @Valid BookingCreateIn dto) {

        if (dto.start.isBefore(dto.end)) {
            return client.create(userId, dto);
        }
        return ResponseEntity.badRequest()
                .body(Map.of("error", "end must be after start"));
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(HEADER_USER) Long ownerId,
                                          @PathVariable @Min(1) Long bookingId,
                                          @RequestParam boolean approved) {
        return client.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER) Long userId,
                                      @PathVariable @Min(1) Long bookingId) {
        return client.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUser(@RequestHeader(HEADER_USER) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return client.getUser(state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwner(@RequestHeader(HEADER_USER) Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return client.getOwner(state, ownerId);
    }
}
