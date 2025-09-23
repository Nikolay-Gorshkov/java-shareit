package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.ItemRepository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookingServiceImplIT {

    @Autowired BookingRepository repo;
    @Autowired UserRepository userRepo;
    @Autowired ItemRepository itemRepo;

    @Test
    void repoQueries_byOwnerAndBooker() {
        User owner = userRepo.save(User.builder().name("o").email("o@e.com").build());
        User booker = userRepo.save(User.builder().name("b").email("b@e.com").build());
        Item item = itemRepo.save(Item.builder().name("i").description("d").available(true).owner(owner).build());

        var past = repo.save(Booking.builder().item(item).booker(booker)
                .status(Booking.Status.APPROVED)
                .start(Instant.parse("2024-01-01T10:00:00Z"))
                .end(Instant.parse("2024-01-01T11:00:00Z")).build());

        List<Booking> byOwner = repo.findByItem_Owner_Id(owner.getId(), org.springframework.data.domain.Sort.by("start").descending());
        assertThat(byOwner).extracting(Booking::getId).contains(past.getId());

        List<Booking> byBooker = repo.findByBooker_IdOrderByStartDesc(booker.getId());
        assertThat(byBooker).isNotEmpty();
    }
}