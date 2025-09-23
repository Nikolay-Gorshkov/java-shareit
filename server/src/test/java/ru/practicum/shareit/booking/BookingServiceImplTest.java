package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock private BookingRepository repo;
    @Mock private UserRepository userRepo;
    @Mock private ItemRepository itemRepo;
    private Clock clock;
    @InjectMocks private BookingServiceImpl service;

    @BeforeEach void init() {
        MockitoAnnotations.openMocks(this);
        clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        service = new BookingServiceImpl(repo, userRepo, itemRepo, clock);
    }

    @Test
    void create_ok() {
        var user = User.builder().id(1L).build();
        var owner = User.builder().id(2L).build();
        var item = Item.builder().id(10L).owner(owner).available(true).name("n").description("d").build();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(10L)).thenReturn(Optional.of(item));

        var start = LocalDateTime.of(2025,1,1,11,0);
        var end   = LocalDateTime.of(2025,1,1,12,0);
        when(repo.save(any())).thenAnswer(a -> {
            Booking b = a.getArgument(0);
            b.setId(100L);
            return b;
        });

        BookingDto res = service.create(1L, BookingCreateDto.builder().itemId(10L).start(start).end(end).build());
        assertThat(res.getId()).isEqualTo(100L);
        assertThat(res.getItem().getId()).isEqualTo(10L);
    }

    @Test
    void create_ownerBookingDenied() {
        var owner = User.builder().id(2L).build();
        var item = Item.builder().id(10L).owner(owner).available(true).build();
        when(userRepo.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(10L)).thenReturn(Optional.of(item));

        var dto = BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2025,1,1,11,0))
                .end(LocalDateTime.of(2025,1,1,12,0)).build();

        assertThatThrownBy(() -> service.create(2L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_badDatesOrUnavailable() {
        var user = User.builder().id(1L).build();
        var owner = User.builder().id(2L).build();
        var item = Item.builder().id(10L).owner(owner).available(false).build();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(10L)).thenReturn(Optional.of(item));

        final var badDates = BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2025,1,1,12,0))
                .end(LocalDateTime.of(2025,1,1,11,0))
                .build();
        assertThatThrownBy(() -> service.create(1L, badDates))
                .isInstanceOf(ValidationException.class);

        final var unavailableItem = BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2025,1,1,11,0))
                .end(LocalDateTime.of(2025,1,1,12,0))
                .build();
        assertThatThrownBy(() -> service.create(1L, unavailableItem))
                .isInstanceOf(ValidationException.class);
    }


    @Test
    void approve_ok_and_conflict() {
        var owner = User.builder().id(2L).build();
        var item = Item.builder().id(10L).owner(owner).build();
        var booking = Booking.builder().id(5L).item(item).booker(User.builder().id(1L).build())
                .status(Booking.Status.WAITING).build();

        when(repo.findById(5L)).thenReturn(Optional.of(booking));
        when(repo.save(any())).thenAnswer(a -> a.getArgument(0));

        assertThat(service.approve(2L, 5L, true).getStatus()).isEqualTo("APPROVED");

        booking.setStatus(Booking.Status.APPROVED);
        assertThatThrownBy(() -> service.approve(2L, 5L, false))
                .isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> service.approve(9L, 5L, true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void get_and_lists() {
        var owner = User.builder().id(2L).build();
        var booker = User.builder().id(1L).build();
        var item = Item.builder().id(10L).owner(owner).build();
        var booking = Booking.builder().id(5L).item(item).booker(booker)
                .status(Booking.Status.APPROVED)
                .start(Instant.parse("2025-01-01T11:00:00Z"))
                .end(Instant.parse("2025-01-01T12:00:00Z")).build();

        when(repo.findById(5L)).thenReturn(Optional.of(booking));

        assertThat(service.get(1L, 5L).getId()).isEqualTo(5L);
        assertThat(service.get(2L, 5L).getId()).isEqualTo(5L);
        assertThatThrownBy(() -> service.get(9L, 5L)).isInstanceOf(NotFoundException.class);

        when(userRepo.findById(1L)).thenReturn(Optional.of(booker));
        when(repo.findByBooker_Id(1L, Sort.by(Sort.Direction.DESC, "start"))).thenReturn(List.of(booking));
        assertThat(service.getUserBookings(1L, "ALL")).hasSize(1);

        when(userRepo.findById(2L)).thenReturn(Optional.of(owner));
        when(repo.findByItem_Owner_Id(2L, Sort.by(Sort.Direction.DESC, "start"))).thenReturn(List.of(booking));
        assertThat(service.getOwnerBookings(2L, "ALL")).hasSize(1);
    }
}
