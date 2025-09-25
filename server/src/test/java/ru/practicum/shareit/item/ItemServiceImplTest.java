package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock private ItemRepository repo;
    @Mock private UserRepository userRepo;
    @Mock private BookingRepository bookingRepo;
    @Mock private CommentRepository commentRepo;
    @Mock private ItemRequestRepository requestRepo;
    private Clock clock;
    @InjectMocks private ItemServiceImpl service;

    @BeforeEach void init() {
        MockitoAnnotations.openMocks(this);
        clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        service = new ItemServiceImpl(repo, userRepo, bookingRepo, commentRepo, clock, requestRepo);
    }

    @Test
    void add_update_get_search() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(repo.save(any())).thenAnswer(a -> {
            Item i = a.getArgument(0);
            if (i.getId() == null) i.setId(5L);
            return i;
        });

        assertThatThrownBy(() -> service.add(1L, ItemDto.builder().description("d").available(true).build()))
                .isInstanceOf(ValidationException.class);

        var created = service.add(1L, ItemDto.builder().name("n").description("d").available(true).build());
        assertThat(created.getId()).isEqualTo(5L);

        var item = Item.builder().id(5L).owner(User.builder().id(1L).build())
                .name("n").description("d").available(true).build();
        when(repo.findById(5L)).thenReturn(Optional.of(item));
        var upd = service.update(1L, 5L, ItemDto.builder().name("n2").build());
        assertThat(upd.getName()).isEqualTo("n2");

        when(repo.findAllByOwner_Id(1L)).thenReturn(List.of(item));
        when(bookingRepo.findByItem_IdInAndStatusAndEndBeforeOrderByEndDesc(any(), eq(Booking.Status.APPROVED), any()))
                .thenReturn(List.of());
        when(bookingRepo.findByItem_IdInAndStatusAndStartAfterOrderByStartAsc(any(), eq(Booking.Status.APPROVED), any()))
                .thenReturn(List.of());
        when(commentRepo.findByItem_IdInOrderByCreatedDesc(any())).thenReturn(List.of());
        assertThat(service.getOwnerItems(1L)).hasSize(1);

        when(repo.search("q")).thenReturn(List.of(item));
        assertThat(service.search("q")).hasSize(1);
        assertThat(service.search(" ")).isEmpty();
    }

    @Test
    void update_forbidden() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(repo.findById(2L)).thenReturn(Optional.of(Item.builder().id(2L).owner(User.builder().id(9L).build()).build()));
        assertThatThrownBy(() -> service.update(1L, 2L, new ItemDto()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void addComment_ok_and_denied() {
        when(bookingRepo.existsByItem_IdAndBooker_IdAndStatusAndEndLessThanEqual(eq(2L), eq(1L), eq(Booking.Status.APPROVED), any()))
                .thenReturn(true);
        when(repo.findById(2L)).thenReturn(Optional.of(Item.builder().id(2L).owner(User.builder().id(9L).build()).build()));
        when(userRepo.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).name("u").build()));
        when(commentRepo.save(any())).thenAnswer(a -> {
            Comment c = a.getArgument(0);
            c.setId(10L);
            return c;
        });

        var out = service.addComment(1L, 2L, CommentDto.builder().text("ok").build());
        assertThat(out.getId()).isEqualTo(10L);

        when(bookingRepo.existsByItem_IdAndBooker_IdAndStatusAndEndLessThanEqual(eq(3L), eq(1L), eq(Booking.Status.APPROVED), any()))
                .thenReturn(false);
        assertThatThrownBy(() -> service.addComment(1L, 3L, CommentDto.builder().text("x").build()))
                .isInstanceOf(ValidationException.class);
    }
}

