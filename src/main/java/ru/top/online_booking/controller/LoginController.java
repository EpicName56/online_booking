package ru.top.online_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        if ("admin".equals(username) && "admin".equals(password)) {
            return "redirect:/admin";
        } else if ("user".equals(username) && "user".equals(password)) {
            return "redirect:/user";
        } else {
            model.addAttribute("error", "Неверный логин или пароль");
            return "login";
        }
    }
}
