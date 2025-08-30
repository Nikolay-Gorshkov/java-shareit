package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repo;
    private final UserService userService;
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public ItemDto add(Long userId, ItemDto dto) {
        ensureUserExists(userId);
        Item item = ItemMapper.fromDto(dto, userId);
        item.setId(seq.incrementAndGet());
        repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto patch) {
        ensureUserExists(userId);
        Item item = getItemOrThrow(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new ForbiddenException("Only owner can edit the item");
        }
        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        if (patch.getRequestId() != null) {
            item.setRequest(patch.getRequestId());
        }
        repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto get(Long itemId, Long requesterId) {
        return ItemMapper.toDto(getItemOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        ensureUserExists(ownerId);
        return repo
                .findAllByOwner(ownerId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        final String q = text.toLowerCase();
        return repo
                .findAvailableByText(q)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void ensureUserExists(Long id) {
        userService.get(id);
    }

    private Item getItemOrThrow(Long itemId) {
        return repo
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
    }
}