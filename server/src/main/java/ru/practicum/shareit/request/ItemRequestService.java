package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private Clock clock = Clock.systemUTC();


    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Description is required");
        }
        User requestor = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        LocalDateTime now = LocalDateTime.now(clock);
        ItemRequest entity = ItemRequestMapper.fromCreateDto(dto, requestor, now);

        entity = requestRepo.save(entity);
        return ItemRequestMapper.toDto(entity);
    }


    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getOwn(Long userId) {
        ensureUser(userId);
        List<ItemRequest> reqs = requestRepo.findAllByRequestor_IdOrderByCreatedDesc(userId);
        if (reqs.isEmpty()) return List.of();
        Map<Long, List<Item>> replies = mapReplies(reqs);
        return reqs.stream()
                .map(r -> ItemRequestMapper.toOutDto(r, replies.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getAll(Long userId, int from, int size) {
        ensureUser(userId);
        if (from < 0 || size < 1) throw new ValidationException("Invalid pagination");

        var pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        var page = requestRepo.findAllByRequestor_IdNot(userId, pageable);

        var content = page.getContent();
        if (content.isEmpty()) return List.of();

        Map<Long, List<Item>> replies = mapReplies(content);
        return content.stream()
                .map(r -> ItemRequestMapper.toOutDto(r, replies.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemRequestOutDto getById(Long userId, Long requestId) {
        ensureUser(userId);
        ItemRequest r = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
        List<Item> items = itemRepo.findAllByRequestOrderByIdAsc(requestId);
        return ItemRequestMapper.toOutDto(r, items);
    }

    private void ensureUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private Map<Long, List<Item>> mapReplies(List<ItemRequest> reqs) {
        List<Long> ids = reqs.stream().map(ItemRequest::getId).toList();
        return itemRepo.findAllByRequestInOrderByIdAsc(ids).stream()
                .collect(Collectors.groupingBy(Item::getRequest));
    }

    void setClock(Clock clock) {
        this.clock = clock != null ? clock : Clock.systemUTC();
    }
}