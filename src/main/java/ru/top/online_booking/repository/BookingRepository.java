package ru.top.online_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.top.online_booking.model.Booking;


import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // найти брони для комнаты в диапазоне, чтобы предотвратить пересечения
    @Query("select b from Booking b where b.room.id = :roomId " +
            "and not (b.endDate < :start or b.startDate > :end)")
    List<Booking> findOverlapping(@Param("roomId") Long roomId,
                                  @Param("start") LocalDate start,
                                  @Param("end") LocalDate end);

    List<Booking> findByRoomId(Long roomId);

    @Query("select case when count(b) > 0 then true else false end " +
            "from Booking b " +
            "where b.room.id = :roomId " +
            "and :today between b.startDate and b.endDate")
    boolean existsActiveBooking(@Param("roomId") Long roomId,
                                @Param("today") LocalDate today);

}
