package ru.practicum.shareit.booking.dto;

public enum BookingState {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("State is null");
        }
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + value);
        }
    }
}
