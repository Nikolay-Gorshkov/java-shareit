package ru.practicum.shareit.booking.dto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto dto);
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);
    BookingDto get(Long userId, Long bookingId);
    List<BookingDto> getUserBookings(Long userId);
}
