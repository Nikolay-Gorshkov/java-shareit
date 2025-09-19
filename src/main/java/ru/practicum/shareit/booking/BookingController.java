package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER_USER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER_USER) Long userId,
                             @RequestBody BookingCreateDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(HEADER_USER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return service.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(HEADER_USER) Long userId, @PathVariable Long bookingId) {
        return service.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(HEADER_USER) Long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(HEADER_USER) Long ownerId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.getOwnerBookings(ownerId, state);
    }
}
