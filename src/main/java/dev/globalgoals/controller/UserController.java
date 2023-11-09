package dev.globalgoals.controller;

import dev.globalgoals.domain.User;
import dev.globalgoals.form.UserForm;
import dev.globalgoals.repository.UserRepository;
import dev.globalgoals.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MailManager mailManager;
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/join";
    }

    @PostMapping("/join")
    public String addItem(@Validated @ModelAttribute UserForm form, BindingResult bindingResult) {
        boolean checked = userService.validateDuplicateMember(form, bindingResult);

        //검증에 실패하면 다시 입력 폼으로
        if (checked) {
            log.info("errors={}", bindingResult);
            return "users/join";
        }

        userService.join(form);
        return "redirect:/";
    }

    @PostMapping("/checkMail") // AJAX와 URL을 매핑시켜줌
    @ResponseBody  //AJAX후 값을 리턴하기위해 작성
    public String SendMail(String email) throws MessagingException {
        UUID uuid = UUID.randomUUID(); // 랜덤한 UUID 생성
        String key = uuid.toString().substring(0, 7); // UUID 문자열 중 7자리만 사용하여 인증번호 생성
        String sub ="인증번호 입력을 위한 메일 전송";
        String con = "인증 번호 : "+key;
        log.error("======{}======",email);
        mailManager.send(email,sub,con);
        return key;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        Object obj = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (obj == null) {
            return "users/login";
        }
            return "redirect:../";
    }
}
