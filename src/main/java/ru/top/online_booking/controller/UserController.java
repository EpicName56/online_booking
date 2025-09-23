package ru.top.online_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.top.online_booking.model.Booking;
import ru.top.online_booking.model.Guest;
import ru.top.online_booking.model.Room;
import ru.top.online_booking.repository.BookingRepository;
import ru.top.online_booking.repository.GuestRepository;
import ru.top.online_booking.repository.RoomRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final RoomRepository roomRepo;
    private final GuestRepository guestRepo;
    private final BookingRepository bookingRepo;

    public UserController(RoomRepository roomRepo, GuestRepository guestRepo, BookingRepository bookingRepo) {
        this.roomRepo = roomRepo;
        this.guestRepo = guestRepo;
        this.bookingRepo = bookingRepo;
    }

    // список комнат
    @GetMapping("/rooms")
    public String rooms(Model model) {
        LocalDate today = LocalDate.now();

        List<Room> rooms = roomRepo.findAll();

        List<Map<String, Object>> roomData = rooms.stream().map(room -> {
            boolean booked = bookingRepo.findByRoomId(room.getId()).stream()
                    .anyMatch(b -> !(b.getEndDate().isBefore(today) || b.getStartDate().isAfter(today)));
            Map<String, Object> map = new HashMap<>();
            map.put("number", room.getNumber());
            map.put("capacity", room.getCapacity());
            map.put("status", booked ? "booked" : "free");
            return map;
        }).toList();

        model.addAttribute("rooms", roomData);
        return "user/rooms";
    }

    // список всех броней
    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingRepo.findAll());
        return "user/my_bookings";
    }

    // форма новой брони
    @GetMapping("/bookings/new")
    public String newBooking(Model model) {
        model.addAttribute("rooms", roomRepo.findAll());
        model.addAttribute("guests", guestRepo.findAll());
        return "user/booking_form";
    }

    // сохранение новой брони
    @PostMapping("/bookings/save")
    public String saveBooking(@RequestParam Long guestId,
                              @RequestParam Long roomId,
                              @RequestParam String startDate,
                              @RequestParam String endDate,
                              Model model) {

        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            model.addAttribute("error", "Пожалуйста, укажите даты бронирования");
            model.addAttribute("rooms", roomRepo.findAll());
            model.addAttribute("guests", guestRepo.findAll());
            return "user/booking_form";
        }

        Guest guest = guestRepo.findById(guestId).orElseThrow();
        Room room = roomRepo.findById(roomId).orElseThrow();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (end.isBefore(start) || end.isEqual(start)) {
            model.addAttribute("error", "Дата выезда должна быть позже даты заезда");
            model.addAttribute("rooms", roomRepo.findAll());
            model.addAttribute("guests", guestRepo.findAll());
            return "user/booking_form";
        }

        // проверка пересечений
        List<Booking> overlaps = bookingRepo.findOverlapping(roomId, start, end);
        if (!overlaps.isEmpty()) {
            model.addAttribute("error", "Эта комната уже забронирована на выбранный период");
            model.addAttribute("rooms", roomRepo.findAll());
            model.addAttribute("guests", guestRepo.findAll());
            return "user/booking_form";
        }

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStartDate(start);
        booking.setEndDate(end);

        bookingRepo.save(booking);

        return "redirect:/user/bookings";
    }

    // форма редактирования
    @GetMapping("/bookings/edit/{id}")
    public String editBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingRepo.findById(id).orElseThrow();
        model.addAttribute("booking", booking);
        model.addAttribute("rooms", roomRepo.findAll());
        model.addAttribute("guests", guestRepo.findAll());
        return "user/booking_form";
    }

    // обновление брони
    @PostMapping("/bookings/update")
    public String updateBooking(@RequestParam Long id,
                                @RequestParam Long guestId,
                                @RequestParam Long roomId,
                                @RequestParam String startDate,
                                @RequestParam String endDate,
                                Model model) {
        Booking booking = bookingRepo.findById(id).orElseThrow();
        Guest guest = guestRepo.findById(guestId).orElseThrow();
        Room room = roomRepo.findById(roomId).orElseThrow();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (end.isBefore(start) || end.isEqual(start)) {
            model.addAttribute("error", "Дата выезда должна быть позже даты заезда");
            model.addAttribute("rooms", roomRepo.findAll());
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("booking", booking);
            return "user/booking_form";
        }

        List<Booking> overlaps = bookingRepo.findOverlapping(roomId, start, end);
        overlaps.removeIf(b -> b.getId().equals(id)); // исключаем текущую бронь

        if (!overlaps.isEmpty()) {
            model.addAttribute("error", "Эта комната уже забронирована на выбранный период");
            model.addAttribute("rooms", roomRepo.findAll());
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("booking", booking);
            return "user/booking_form";
        }

        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStartDate(start);
        booking.setEndDate(end);

        bookingRepo.save(booking);

        return "redirect:/user/bookings";
    }

    // удаление брони
    @PostMapping("/bookings/delete")
    public String deleteBooking(@RequestParam Long id) {
        bookingRepo.deleteById(id);
        return "redirect:/user/bookings";
    }

    // главная страница
    @GetMapping
    public String home() {
        return "user/index";
    }
}
