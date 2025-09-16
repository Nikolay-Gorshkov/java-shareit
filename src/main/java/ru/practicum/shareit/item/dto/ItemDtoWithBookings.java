package ru.practicum.shareit.item.dto;

import lombok.*;
import java.util.List;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemDtoWithBookings extends ItemDto {
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private List<CommentDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingShort {
        private Long id;
        private Long bookerId;
    }
}
