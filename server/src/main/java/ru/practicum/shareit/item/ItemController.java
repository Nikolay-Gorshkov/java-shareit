package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String HEADER_USER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto add(@RequestHeader(HEADER_USER) Long userId, @RequestBody @Valid ItemDto dto) {
        log.info("POST /items userId={} body={}", userId, dto);
        return service.add(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patch) {
        log.info("PATCH /items/{} userId={} body={}", itemId, userId, patch);
        return service.update(userId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings get(@RequestHeader(HEADER_USER) Long userId,
                                   @PathVariable Long itemId) {
        log.info("GET /items/{} userId={}", itemId, userId);
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getOwnerItems(@RequestHeader(HEADER_USER) Long userId) {
        log.info("GET /items userId={}", userId);
        return service.getOwnerItems(userId);
    }


    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("GET /items/search text='{}'", text);
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER_USER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto dto) {
        return service.addComment(userId, itemId, dto);
    }

}
