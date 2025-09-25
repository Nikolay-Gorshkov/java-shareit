package ru.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.item.dto.ItemDtoIn;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER_USER) Long userId,
                                      @RequestBody @Valid ItemDtoIn dto) {
        if (dto.requestId != null && dto.requestId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "requestId must be positive"));
        }
        return client.add(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader(HEADER_USER) Long userId,
                                        @PathVariable @Min(1) Long itemId,
                                        @RequestBody Map<String, Object> patch) {
        if (patch.containsKey("requestId")) {
            Object v = patch.get("requestId");
            if (v != null) {
                long id = v instanceof Number n ? n.longValue() : Long.parseLong(v.toString());
                if (id <= 0) return ResponseEntity.badRequest().body(Map.of("error", "requestId must be positive"));
            }
        }
        return client.patch(userId, itemId, new HashMap<>(patch));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER) Long userId,
                                      @PathVariable @Min(1) Long itemId) {
        return client.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(HEADER_USER) Long userId) {
        return client.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        if (text == null || text.isBlank()) return ResponseEntity.ok().body(java.util.List.of());
        return client.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER_USER) Long userId,
                                             @PathVariable @Min(1) Long itemId,
                                             @RequestBody Map<String, Object> dto) {
        Object text = dto.get("text");
        if (text == null || text.toString().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Текст комментария обязателен"));
        }
        return client.addComment(userId, itemId, dto);
    }

}

