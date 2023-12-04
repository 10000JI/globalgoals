package dev.globalgoals.controller;

import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.StampCard;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.StampCardWithGoalDto;
import dev.globalgoals.dto.UserDTO;
import dev.globalgoals.repository.UserRepository;
import dev.globalgoals.service.BoardService;
import dev.globalgoals.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MailManager mailManager;

    private final BoardService boardService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "users/join";
    }

    @PostMapping("/join")
    public String addItem(@Validated @ModelAttribute UserDTO userDTO, BindingResult bindingResult) {
        boolean checked = userService.validateDuplicateMember(userDTO, bindingResult);

        //검증에 실패하면 다시 입력 폼으로
        if (checked) {
            log.info("errors={}", bindingResult);
            return "users/join";
        }

        userService.join(userDTO);
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
    public String loginForm(HttpSession session, Model model, HttpServletRequest request) {
        Object obj = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (obj == null) {
            // 쿠키 값을 모델에 추가
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember".equals(cookie.getName())) {
                        model.addAttribute("rememberValue", cookie.getValue());
                        break;
                    }
                }
            }
            return "users/login";
        }
        return "redirect:../";
    }

    @GetMapping("/mypage")
    public String myPageForm(Model model) {
        model.addAttribute("userForm", new UserDTO());
        return "users/myPage";
    }

    @GetMapping("/mypage/stamp")
    public String myStampForm(Principal principal, Model model) {
        List<StampCardWithGoalDto> stampCardWithGoals = userService.getStampCardWithGoalDtos(principal);
        model.addAttribute("stampCardWithGoals", stampCardWithGoals);
        return "users/stamp";
    }

    // 자유 게시판 & 실천 방법 등록 & 실천 등록 & 전체 글보기 리스트
    @GetMapping("/{cate}/list")
    public String list(PageRequestDTO requestDTO, Model model, @PathVariable String cate, Principal principal) {
        model.addAttribute("result", boardService.getList(requestDTO, cate, principal));
        model.addAttribute("cate", cate);
        return "users/mainList";
    }
}
