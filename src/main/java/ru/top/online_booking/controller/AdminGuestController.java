package ru.top.online_booking.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.top.online_booking.model.Guest;
import ru.top.online_booking.repository.GuestRepository;

@Controller
@RequestMapping("/admin/guests")
public class AdminGuestController {
    private final GuestRepository guestRepo;

    public AdminGuestController(GuestRepository guestRepo) {
        this.guestRepo = guestRepo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("guests", guestRepo.findAll());
        return "admin/guests/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("guest", new Guest());
        return "admin/guests/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("guest", guestRepo.findById(id).orElseThrow());
        return "admin/guests/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Guest guest, BindingResult br) {
        if (br.hasErrors()) return "admin/guests/form";
        guestRepo.save(guest);
        return "redirect:/admin/guests";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        guestRepo.deleteById(id);
        return "redirect:/admin/guests";
    }
}
