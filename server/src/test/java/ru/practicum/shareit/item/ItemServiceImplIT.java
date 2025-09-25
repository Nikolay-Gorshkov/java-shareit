package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceImplIT {

    @Autowired ItemService service;
    @Autowired ItemRepository itemRepo;
    @Autowired UserRepository userRepo;
    @Autowired BookingRepository bookingRepo;

    @Test
    void getOwnerItems_withLastAndNext() {
        User owner = userRepo.save(User.builder().name("o").email("o@e.com").build());
        User booker = userRepo.save(User.builder().name("b").email("b@e.com").build());
        Item item = itemRepo.save(Item.builder().name("drill").description("desc").available(true).owner(owner).build());

        bookingRepo.save(Booking.builder()
                .item(item).booker(booker).status(Booking.Status.APPROVED)
                .start(Instant.parse("2024-01-01T10:00:00Z"))
                .end(Instant.parse("2024-01-01T11:00:00Z")).build());
        bookingRepo.save(Booking.builder()
                .item(item).booker(booker).status(Booking.Status.APPROVED)
                .start(Instant.parse("2099-01-01T10:00:00Z"))
                .end(Instant.parse("2099-01-01T11:00:00Z")).build());

        var list = service.getOwnerItems(owner.getId());
        assertThat(list).hasSize(1);
        ItemDtoWithBookings d = list.get(0);
        assertThat(d.getLastBooking()).isNotNull();
        assertThat(d.getNextBooking()).isNotNull();
    }

    @Test
    void addComment_requiresFinishedApprovedBooking() {
        var owner = userRepo.save(ru.practicum.shareit.user.User.builder().name("o").email("o@e.com").build());
        var booker = userRepo.save(ru.practicum.shareit.user.User.builder().name("b").email("b@e.com").build());
        var item = itemRepo.save(ru.practicum.shareit.item.model.Item.builder()
                .name("drill").description("desc").available(true).owner(owner).build());

        bookingRepo.save(ru.practicum.shareit.booking.model.Booking.builder()
                .item(item)
                .booker(booker)
                .status(ru.practicum.shareit.booking.model.Booking.Status.APPROVED)
                .start(java.time.Instant.parse("2024-01-01T10:00:00Z"))
                .end(java.time.Instant.parse("2024-01-01T11:00:00Z"))
                .build());

        var out = service.addComment(booker.getId(), item.getId(),
                ru.practicum.shareit.item.dto.CommentDto.builder().text("ok").build());
        org.assertj.core.api.Assertions.assertThat(out.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(out.getText()).isEqualTo("ok");

        var stranger = userRepo.save(ru.practicum.shareit.user.User.builder().name("s").email("s@e.com").build());
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        service.addComment(stranger.getId(), item.getId(),
                                ru.practicum.shareit.item.dto.CommentDto.builder().text("nope").build()))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

}
