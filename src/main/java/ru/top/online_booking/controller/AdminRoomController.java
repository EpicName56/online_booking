package ru.top.online_booking.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.top.online_booking.model.Room;
import ru.top.online_booking.repository.RoomRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {
    private final RoomRepository roomRepo;

    public AdminRoomController(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    @GetMapping
    public String list(Model model) {
        List<Room> rooms = roomRepo.findAll();
        model.addAttribute("rooms", rooms);
        return "admin/rooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new Room());
        return "admin/rooms/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomRepo.findById(id).orElseThrow());
        return "admin/rooms/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Room room, BindingResult br) {
        if (br.hasErrors()) return "admin/rooms/form";
        roomRepo.save(room);
        return "redirect:/admin/rooms";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        roomRepo.deleteById(id);
        return "redirect:/admin/rooms";
    }
}
