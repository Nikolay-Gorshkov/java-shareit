package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto dto) {
        log.info("POST /users body={}", dto);
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        log.info("PATCH /users/{} body={}", id, dto);
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("GET /users/{}", id);
        return service.get(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET /users");
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        service.delete(id);
    }
}
