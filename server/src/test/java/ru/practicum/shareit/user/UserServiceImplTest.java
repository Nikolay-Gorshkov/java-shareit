package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository repo;
    @InjectMocks private UserServiceImpl service;

    @BeforeEach void init(){ MockitoAnnotations.openMocks(this); }

    @Test
    void create_ok() {
        var in = UserDto.builder().name("n").email("e@x.com").build();
        when(repo.existsByEmailIgnoreCase("e@x.com")).thenReturn(false);
        when(repo.save(any())).thenAnswer(a -> {
            User u = a.getArgument(0);
            u.setId(1L);
            return u;
        });

        var out = service.create(in);
        assertThat(out.getId()).isEqualTo(1L);
    }

    @Test
    void create_invalidEmail() {
        var in = UserDto.builder().name("n").email("bad").build();
        assertThatThrownBy(() -> service.create(in))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_conflict() {
        var in = UserDto.builder().name("n").email("e@x.com").build();
        when(repo.existsByEmailIgnoreCase("e@x.com")).thenReturn(true);
        assertThatThrownBy(() -> service.create(in))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void update_ok() {
        when(repo.findById(5L)).thenReturn(Optional.of(User.builder().id(5L).name("a").email("a@a").build()));
        when(repo.existsByEmailIgnoreCaseAndIdNot("b@b", 5L)).thenReturn(false);
        when(repo.save(any())).thenAnswer(a -> a.getArgument(0));

        var out = service.update(5L, UserDto.builder().email("b@b").name("b").build());
        assertThat(out.getEmail()).isEqualTo("b@b");
        assertThat(out.getName()).isEqualTo("b");
    }

    @Test
    void update_notFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(1L, new UserDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void get_delete_getAll() {
        when(repo.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).name("n").email("e@x").build()));
        assertThat(service.get(2L).getId()).isEqualTo(2L);

        when(repo.findById(3L)).thenReturn(Optional.of(User.builder().id(3L).build()));
        service.delete(3L);
        verify(repo).deleteById(3L);

        when(repo.findAll()).thenReturn(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()));
        assertThat(service.getAll()).hasSize(2);
    }
}