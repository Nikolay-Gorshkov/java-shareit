package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRequestServiceIT {

    @Autowired ItemRequestService service;
    @Autowired UserRepository userRepo;

    @Test
    void create_and_getOwn() {
        User u = userRepo.save(User.builder().name("u").email("u@e.com").build());
        var created = service.create(u.getId(), ItemRequestDto.builder().description("need drill").build());
        assertThat(created.getId()).isNotNull();

        var own = service.getOwn(u.getId());
        assertThat(own).hasSize(1);
        assertThat(own.get(0).getDescription()).isEqualTo("need drill");
        assertThat(own.get(0).getItems()).isEmpty();
    }
}
