package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;

    @Override
    public ItemDto add(Long userId, ItemDto dto) {
        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (dto.getName() == null || dto.getName().isBlank())
            throw new ValidationException("Item name is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new ValidationException("Item description is required");
        if (dto.getAvailable() == null)
            throw new ValidationException("Item availability is required");

        Item item = ItemMapper.fromDto(dto, owner);
        item = repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto patch) {
        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        if (!item.getOwner().getId().equals(owner.getId()))
            throw new ForbiddenException("Only owner can edit the item");

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
        item = repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDtoWithBookings> getOwnerItems(Long ownerId) {
        userRepo.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found: " + ownerId));
        return repo.findAllByOwner_Id(ownerId).stream()
                .map(i -> toItemDtoWithBookings(i.getId(), ownerId))
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return repo.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new ValidationException("Comment text is required");
        }

        LocalDateTime moment = LocalDateTime.now().plusHours(3).plusSeconds(1);
        boolean allowed = bookingRepo.hasUserFinishedApprovedBooking(itemId, userId, moment);

        if (!allowed) {
            throw new ValidationException("Only users who completed approved booking can comment");
        }
        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        comment = commentRepo.save(comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    public ItemDtoWithBookings get(Long itemId, Long requesterId) {
        return toItemDtoWithBookings(itemId, requesterId);
    }


    private ItemDtoWithBookings toItemDtoWithBookings(Long itemId, Long requesterId) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        ItemDtoWithBookings dto = new ItemDtoWithBookings();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest());

        var comments = commentRepo.findByItem_IdOrderByCreatedDesc(itemId)
                .stream().map(CommentMapper::toDto).collect(Collectors.toList());
        dto.setComments(comments);

        if (item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepo.findTop1ByItem_IdAndStatusAndEndBeforeOrderByEndDesc(
                    itemId, Booking.Status.APPROVED, now
            ).stream().findFirst().ifPresent(b ->
                    dto.setLastBooking(new ItemDtoWithBookings.BookingShort(b.getId(), b.getBooker().getId()))
            );

            bookingRepo.findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, Booking.Status.APPROVED, now
            ).stream().findFirst().ifPresent(b ->
                    dto.setNextBooking(new ItemDtoWithBookings.BookingShort(b.getId(), b.getBooker().getId()))
            );
        } else {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }

        return dto;
    }
}
