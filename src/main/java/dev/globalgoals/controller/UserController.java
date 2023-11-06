package dev.globalgoals.controller;

import dev.globalgoals.form.UserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/users/login")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new UserForm());
        return "users/login";
    }
}
