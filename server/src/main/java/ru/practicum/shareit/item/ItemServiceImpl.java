package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.request.ItemRequestRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;
    private final Clock clock;
    private final ItemRequestRepository requestRepo;

    @Override
    public ItemDto add(Long userId, ItemDto dto) {
        User owner = getUserOrThrow(userId);

        if (dto.getName() == null || dto.getName().isBlank())
            throw new ValidationException("Item name is required");
        if (dto.getDescription() == null || dto.getDescription().isBlank())
            throw new ValidationException("Item description is required");
        if (dto.getAvailable() == null)
            throw new ValidationException("Item availability is required");

        if (dto.getRequestId() != null && !requestRepo.existsById(dto.getRequestId())) {
            throw new NotFoundException("Request not found: " + dto.getRequestId());
        }

        var item = ItemMapper.fromDto(dto, owner);
        item = repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto patch) {
        User owner = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

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
            if (!requestRepo.existsById(patch.getRequestId())) {
                throw new NotFoundException("Request not found: " + patch.getRequestId());
            }
          item.setRequest(patch.getRequestId());
        }
        item = repo.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDtoWithBookings> getOwnerItems(Long ownerId) {
        getUserOrThrow(ownerId);

        List<Item> items = repo.findAllByOwner_Id(ownerId);
        if (items.isEmpty()) return List.of();

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Instant now = Instant.now(clock); // <— INSTANT, не LDT

        var comments = commentRepo.findByItem_IdInOrderByCreatedDesc(itemIds);
        var commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        var lastBookings = bookingRepo.findByItem_IdInAndStatusAndEndBeforeOrderByEndDesc(
                itemIds, Booking.Status.APPROVED, now
        );
        var lastByItem = lastBookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> new ItemDtoWithBookings.BookingShort(b.getId(), b.getBooker().getId()),
                        (existing, ignore) -> existing
                ));

        var nextBookings = bookingRepo.findByItem_IdInAndStatusAndStartAfterOrderByStartAsc(
                itemIds, Booking.Status.APPROVED, now
        );
        var nextByItem = nextBookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> new ItemDtoWithBookings.BookingShort(b.getId(), b.getBooker().getId()),
                        (existing, ignore) -> existing
                ));

        return items.stream()
                .map(it -> {
                    var dto = ItemMapper.toDtoWithBookingsSkeleton(it);
                    dto.setComments(commentsByItem.getOrDefault(it.getId(), List.of()));
                    dto.setLastBooking(lastByItem.get(it.getId()));
                    dto.setNextBooking(nextByItem.get(it.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return repo.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new ValidationException("Текст комментария обязателен");
        }

        Instant now = Instant.now(clock);

        // ⚙️ Основная проверка — по времени приложения
        boolean allowedByAppClock = bookingRepo
                .existsByItem_IdAndBooker_IdAndStatusAndEndLessThanEqual(
                        itemId, userId, Booking.Status.APPROVED, now);

        // ⚙️ Резервная проверка — по времени БД (CURRENT_TIMESTAMP)
        boolean allowedByDbClock = bookingRepo.hasFinishedApprovedBooking(itemId, userId);

        boolean allowed = allowedByAppClock || allowedByDbClock;

        if (!allowed) {
            log.debug("COMMENT-CHECK FAIL itemId={}, userId={}, now={}", itemId, userId, now);

            bookingRepo.findByItem_IdAndBooker_Id(itemId, userId)
                    .forEach(b -> log.debug("  booking id={} status={} start={} end={}",
                            b.getId(), b.getStatus(), b.getStart(), b.getEnd()));

            throw new ValidationException("Оставить комментарий можно только после завершения бронирования");
        }


        Item item = getItemOrThrow(itemId);
        User author = getUserOrThrow(userId);
        LocalDateTime created = LocalDateTime.now(clock);

        Comment saved = commentRepo.save(CommentMapper.from(dto.getText(), item, author, created));
        return CommentMapper.toDto(saved);
    }


    @Override
    public ItemDtoWithBookings get(Long itemId, Long requesterId) {
        return toItemDtoWithBookings(itemId, requesterId);
    }

    private ItemDtoWithBookings toItemDtoWithBookings(Long itemId, Long requesterId) {
        Item item = getItemOrThrow(itemId);
        var dto = ItemMapper.toDtoWithBookingsSkeleton(item);

        var comments = commentRepo.findByItem_IdOrderByCreatedDesc(itemId)
                .stream().map(CommentMapper::toDto).toList();
        dto.setComments(comments);

        if (item.getOwner().getId().equals(requesterId)) {
            Instant now = Instant.now(clock);

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

    private User getUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
    }
}
