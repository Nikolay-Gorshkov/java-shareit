package ru.practicum.shareit.booking.dto;

public class BookingMapper {
    public static BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .itemId(b.getItem().getId())
                .bookerId(b.getBooker().getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus().name())
                .build();
    }
}