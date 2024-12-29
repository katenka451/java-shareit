package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.booker as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.booker as u " +
            "where u.id = ?1 " +
            " and b.start <= ?2 " +
            " and b.end >= ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.booker as u " +
            "where u.id = ?1 " +
            " and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.booker as u " +
            "where u.id = ?1 " +
            " and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.booker as u " +
            "where u.id = ?1 " +
            " and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where i.ownerId = ?1 " +
            "order by b.start desc")
    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where i.ownerId = ?1 " +
            " and b.start <= ?2 " +
            " and b.end >= ?2 " +
            "order by b.start desc")
    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where i.ownerId = ?1 " +
            " and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where i.ownerId = ?1 " +
            " and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where i.ownerId = ?1 " +
            " and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeOrderByStartDesc(Long itemId,
                                                                        LocalDateTime now);

    List<Booking> findFirstByItemIdInAndStartIsBeforeOrderByStartDesc(List<Long> itemId,
                                                                      LocalDateTime now);


    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStart(Long itemId,
                                                                   LocalDateTime now);

    List<Booking> findFirstByItemIdInAndStartIsAfterOrderByStart(List<Long> itemId,
                                                                 LocalDateTime now);


    boolean existsByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime now);
}
