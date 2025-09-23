package ru.top.online_booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.top.online_booking.model.Booking;
import ru.top.online_booking.repository.BookingRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> listAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional
    public Booking create(Booking booking) {
        // простая валидация дат
        if (booking.getEndDate().isBefore(booking.getStartDate()) ||
                booking.getEndDate().isEqual(booking.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        // проверить пересечение
        List<Booking> overlap = bookingRepository.findOverlapping(
                booking.getRoom().getId(), booking.getStartDate(), booking.getEndDate());
        if (!overlap.isEmpty()) {
            throw new IllegalStateException("Room already booked in this period");
        }
        return bookingRepository.save(booking);
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    @Service
    public class RoomStatusService {
        private final BookingRepository bookingRepo;

        public RoomStatusService(BookingRepository bookingRepo) {
            this.bookingRepo = bookingRepo;
        }

        public String getRoomStatus(Long roomId) {
            boolean booked = bookingRepo.existsActiveBooking(roomId, LocalDate.now());
            return booked ? "booked" : "free";
        }
    }

}
