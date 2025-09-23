package ru.top.online_booking.initializer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.top.online_booking.model.Guest;
import ru.top.online_booking.model.Room;
import ru.top.online_booking.repository.GuestRepository;
import ru.top.online_booking.repository.RoomRepository;


@Component
public class DataInitializer {
    private final GuestRepository guestRepo;
    private final RoomRepository roomRepo;

    public DataInitializer(GuestRepository guestRepo, RoomRepository roomRepo) {
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
    }

    @PostConstruct
    public void init() {
        if (guestRepo.count() == 0) {
            guestRepo.save(new Guest("Ivan Ivanov", "ivan@example.com"));
            guestRepo.save(new Guest("Olga Petrova", "olga@example.com"));
            guestRepo.save(new Guest("Alexey Ivanov", "alexey@example.com"));
        }
        if (roomRepo.count() == 0) {
            roomRepo.save(new Room("101", 2));
            roomRepo.save(new Room("102", 3));
            roomRepo.save(new Room("103", 1));
            roomRepo.save(new Room("104", 1));
        }
    }
}
