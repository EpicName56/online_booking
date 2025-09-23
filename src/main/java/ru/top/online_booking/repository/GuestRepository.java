package ru.top.online_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.top.online_booking.model.Guest;


public interface GuestRepository extends JpaRepository<Guest, Long> {
}
