package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    private ItemShortDto item;

    private UserShortDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private String status;
}
