package ru.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingCreateIn {
    @NotNull
    public Long itemId;

    @NotNull
    public LocalDateTime start;

    @NotNull
    public LocalDateTime end;
}

