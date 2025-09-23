package ru.practicum.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.UserClient;

import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient client;

    public static class UserIn {
        public Long id;
        @NotBlank public String name;
        @NotBlank @Email public String email;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserIn dto) {
        return client.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@PathVariable @Min(1) Long id,
                                        @RequestBody Map<String, Object> patch) {
        if (patch.containsKey("email")) {
            Object e = patch.get("email");
            if (e == null || !e.toString().contains("@")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid email"));
            }
        }
        return client.patch(id, patch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable @Min(1) Long id) {
        return client.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable @Min(1) Long id) {
        return client.delete(id);
    }
}