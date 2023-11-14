package dev.globalgoals.controller;

import dev.globalgoals.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    @GetMapping("/main")
    public String joinForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "/board/mainboard";
    }
}
