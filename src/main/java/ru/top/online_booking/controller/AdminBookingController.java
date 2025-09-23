package ru.top.online_booking.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.top.online_booking.model.Booking;
import ru.top.online_booking.model.Guest;
import ru.top.online_booking.model.Room;
import ru.top.online_booking.repository.BookingRepository;
import ru.top.online_booking.repository.GuestRepository;
import ru.top.online_booking.repository.RoomRepository;
import ru.top.online_booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {
    private final BookingRepository bookingRepo;
    private final GuestRepository guestRepo;
    private final RoomRepository roomRepo;
    private final BookingService bookingService;

    public AdminBookingController(BookingRepository bookingRepo, GuestRepository guestRepo,
                                  RoomRepository roomRepo, BookingService bookingService) {
        this.bookingRepo = bookingRepo;
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingRepo.findAll());
        return "admin/bookings/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("guests", guestRepo.findAll());
        model.addAttribute("rooms", roomRepo.findAll());
        return "admin/bookings/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Booking booking,
                       @RequestParam Long guestId,
                       @RequestParam Long roomId,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                       Model model) {

        Guest guest = guestRepo.findById(guestId).orElse(null);
        Room room = roomRepo.findById(roomId).orElse(null);

        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        if (startDate == null || endDate == null) {
            model.addAttribute("error", "Пожалуйста, укажите даты бронирования");
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("rooms", roomRepo.findAll());
            return "admin/bookings/form";
        }

        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            model.addAttribute("error", "Дата выезда должна быть позже даты заезда");
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("rooms", roomRepo.findAll());
            return "admin/bookings/form";
        }

        List<Booking> overlaps = bookingRepo.findOverlapping(roomId, startDate, endDate);
        if (!overlaps.isEmpty()) {
            model.addAttribute("error", "Эта комната уже забронирована на выбранный период");
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("rooms", roomRepo.findAll());
            return "admin/bookings/form";
        }

        try {
            bookingService.create(booking);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("guests", guestRepo.findAll());
            model.addAttribute("rooms", roomRepo.findAll());
            return "admin/bookings/form";
        }

        return "redirect:/admin/bookings";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        bookingService.delete(id);
        return "redirect:/admin/bookings";
    }
}
