package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    private static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User booker = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("Owner cannot book own item");

        if (dto.getStart() == null || dto.getEnd() == null || !dto.getStart().isBefore(dto.getEnd()))
            throw new ValidationException("Invalid booking dates");

        if (Boolean.FALSE.equals(item.getAvailable()))
            throw new ValidationException("Item not available for booking");

        var booking = BookingMapper.fromCreateDto(dto, item, booker);
        booking = repo.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        var booking = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        if (!booking.getItem().getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Only owner can approve/reject booking");
        if (booking.getStatus() != Booking.Status.WAITING)
            throw new ConflictException("Booking already processed");

        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        return BookingMapper.toDto(repo.save(booking));
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        var booking = repo.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Long ownerId = booking.getItem().getOwner().getId();
        if (!booking.getBooker().getId().equals(userId) && !ownerId.equals(userId))
            throw new NotFoundException("Booking not available to user"); // как в условии
        return BookingMapper.toDto(booking);
    }


    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateStr) {
        userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = switch (state) {
            case ALL -> repo.findByBooker_Id(userId, SORT_DESC);
            case CURRENT -> repo.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, SORT_DESC);
            case PAST -> repo.findByBooker_IdAndEndBefore(userId, now, SORT_DESC);
            case FUTURE -> repo.findByBooker_IdAndStartAfter(userId, now, SORT_DESC);
            case WAITING -> repo.findByBooker_IdAndStatus(userId, Booking.Status.WAITING, SORT_DESC);
            case REJECTED -> repo.findByBooker_IdAndStatus(userId, Booking.Status.REJECTED, SORT_DESC);
        };
        return result.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String stateStr) {
        userRepo.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found: " + ownerId));
        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = switch (state) {
            case ALL -> repo.findByItem_Owner_Id(ownerId, SORT_DESC);
            case CURRENT -> repo.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, SORT_DESC);
            case PAST -> repo.findByItem_Owner_IdAndEndBefore(ownerId, now, SORT_DESC);
            case FUTURE -> repo.findByItem_Owner_IdAndStartAfter(ownerId, now, SORT_DESC);
            case WAITING -> repo.findByItem_Owner_IdAndStatus(ownerId, Booking.Status.WAITING, SORT_DESC);
            case REJECTED -> repo.findByItem_Owner_IdAndStatus(ownerId, Booking.Status.REJECTED, SORT_DESC);
        };
        return result.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }
}

