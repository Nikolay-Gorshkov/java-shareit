package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    // 🔧 Добавили недостающий мок
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService service;

    private User user;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        long userId = 1L;

        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("user@test.com")
                .build();

        request1 = ItemRequest.builder()
                .id(10L)
                .description("Need drill")
                .requestor(user)
                .created(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        request2 = ItemRequest.builder()
                .id(11L)
                .description("Need hammer")
                .requestor(user)
                .created(LocalDateTime.of(2024, 1, 2, 10, 0))
                .build();

        // фиксированное время, чтобы не было NPE и результаты были детерминированы
        Clock fixed = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        service.setClock(fixed);

        // моки userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(userRepository.existsById(999L)).thenReturn(false);

        // 🔧 Заглушка для подгрузки ответов на запросы (чтобы не падало внутри mapReplies)
        when(itemRepository.findAllByRequestInOrderByIdAsc(anyList()))
                .thenReturn(List.of()); // если хочешь — можешь вернуть список Item
    }

    @Test
    void create_and_getOwn_and_getAll_and_getById() {
        // create
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request1);
        var created = service.create(1L, ItemRequestDto.builder().description("Need drill").build());
        assertThat(created.getId()).isEqualTo(10L);

        // getOwn
        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request1, request2));
        List<ItemRequestOutDto> own = service.getOwn(1L);
        assertThat(own).hasSize(2);

        // getAll (страница чужих запросов)
        when(itemRequestRepository.findAllByRequestor_IdNot(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(request2, request1),
                        PageRequest.of(0, 10),
                        2
                ));

        // (страховка, если сервис вдруг вызовет общий findAll)
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(request2, request1),
                        PageRequest.of(0, 10),
                        2
                ));

        List<ItemRequestOutDto> all = service.getAll(1L, 0, 10);
        assertThat(all).isNotEmpty();


        // getById
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(request1));
        var byId = service.getById(1L, 10L);
        assertThat(byId.getId()).isEqualTo(10L);
    }

    @Test
    void getById_notFound() {
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }
}