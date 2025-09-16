package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Booking.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findByBooker_IdAndEndBefore(
            Long bookerId,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findByBooker_IdAndStartAfter(
            Long bookerId,
            LocalDateTime start,
            Sort sort
    );

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort); // WAITING/REJECTED

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findByItem_Owner_IdAndEndBefore(
            Long ownerId,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findByItem_Owner_IdAndStartAfter(
            Long ownerId,
            LocalDateTime start,
            Sort sort
    );

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Status status, Sort sort);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndLessThanEqual(
            Long itemId,
            Long bookerId,
            Booking.Status status,
            LocalDateTime now
    );

    List<Booking> findTop1ByItem_IdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId,
            Booking.Status status,
            LocalDateTime now
    );

    List<Booking> findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId,
            Booking.Status status,
            LocalDateTime now
    );

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_IdInOrderByStartDesc(List<Long> itemIds);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            Booking.Status status,
            LocalDateTime moment
    );

    @Query("""
            select case when count(b) > 0 then true else false end
            from Booking b
            where b.item.id = :itemId
              and b.booker.id = :userId
              and b.status = ru.practicum.shareit.booking.model.Booking.Status.APPROVED
              and b.end <= :moment
            """)
    boolean hasUserFinishedApprovedBooking(
            @Param("itemId") Long itemId,
            @Param("userId") Long userId,
            @Param("moment") LocalDateTime moment
    );
}