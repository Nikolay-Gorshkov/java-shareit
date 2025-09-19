package ru.practicum.shareit.booking;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    List<Booking> findByItem_IdInAndStatusAndEndBeforeOrderByEndDesc(
            List<Long> itemIds,
            Booking.Status status,
            Instant now
    );

    List<Booking> findByItem_IdInAndStatusAndStartAfterOrderByStartAsc(
            List<Long> itemIds,
            Booking.Status status,
            Instant now
    );


    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(
            Long bookerId, Instant start, Instant end, Sort sort);

    List<Booking> findByBooker_IdAndEndBefore(
            Long bookerId, Instant end, Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(
            Long bookerId, Instant start, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Booking.Status status, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    boolean existsByItem_IdAndBooker_IdAndStatus(Long itemId, Long bookerId, Booking.Status status);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long ownerId, Instant start, Instant end, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndBefore(
            Long ownerId, Instant end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartAfter(
            Long ownerId, Instant start, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Booking.Status status, Sort sort);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndLessThanEqual(
            Long itemId, Long bookerId, Booking.Status status, Instant now);

    List<Booking> findTop1ByItem_IdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId, Booking.Status status, Instant now);

    List<Booking> findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, Booking.Status status, Instant now);

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_IdInOrderByStartDesc(List<Long> itemIds);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
            Long itemId, Long bookerId, Booking.Status status, Instant moment);

    @Query("""
        select (count(b) > 0)
        from Booking b
        where b.item.id = :itemId
          and b.booker.id = :userId
          and b.status = ru.practicum.shareit.booking.model.Booking.Status.APPROVED
          and b.end <= CURRENT_TIMESTAMP
    """)
    boolean hasFinishedApprovedBooking(Long itemId, Long userId);

    boolean existsByItem_IdAndBooker_Id(Long itemId, Long userId);

    // ВАЖНО: тут была опечатка b.endDate -> b.end, и тип времени -> Instant
    @Query("""
        select (count(b) > 0)
        from Booking b
        where b.item.id = :itemId
          and b.booker.id = :userId
          and b.status = :status
          and b.end <= :now
    """)
    boolean existsPastApprovedBooking(@Param("itemId") Long itemId,
                                      @Param("userId") Long userId,
                                      @Param("status") Booking.Status status,
                                      @Param("now") Instant now);

}
