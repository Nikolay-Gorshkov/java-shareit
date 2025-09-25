package ru.practicum.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.RequestClient;
import ru.practicum.request.dto.ItemRequestCreateIn;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private static final String HEADER_USER = "X-Sharer-User-Id";
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER) Long userId,
                                         @RequestBody @Valid ItemRequestCreateIn dto) {
        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(HEADER_USER) Long userId) {
        return client.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_USER) Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_USER) Long userId,
                                          @PathVariable @Min(1) Long requestId) {
        return client.getById(userId, requestId);
    }
}
