package ru.top.online_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.top.online_booking.model.Room;


public interface RoomRepository extends JpaRepository<Room, Long> {
}
