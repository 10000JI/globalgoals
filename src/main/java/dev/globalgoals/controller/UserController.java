package dev.globalgoals.controller;

import dev.globalgoals.dto.*;
import dev.globalgoals.service.BoardService;
import dev.globalgoals.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
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


    //회원가입
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "users/join";
    }

    //회원가입
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

    //이메일 인증 (join)
    @PostMapping("/checkMail") // AJAX와 URL을 매핑시켜줌
    @ResponseBody  //AJAX후 값을 리턴하기위해 작성
    public String SendMail(String email) throws MessagingException {
        String key = getEmail(email);
        return key;
    }

//    //이메일 인증 (myPage, 본인 인증)
//    @PostMapping("/checkMeMail") // AJAX와 URL을 매핑시켜줌
//    @ResponseBody  //AJAX후 값을 리턴하기위해 작성
//    public String SendMeMail(String email) throws MessagingException {
//        if (userService.validateEmail(email)) {
//            return "false";
//        }
//        String key = getEmail(email);
//        return key;
//    }

    @NotNull
    private String getEmail(String email) throws MessagingException {
        UUID uuid = UUID.randomUUID(); // 랜덤한 UUID 생성
        String key = uuid.toString().substring(0, 7); // UUID 문자열 중 7자리만 사용하여 인증번호 생성
        String sub ="인증번호 입력을 위한 메일 전송";
        String con = "인증 번호 : "+key;
        log.error("======{}======", email);
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

    //마이페이지
    @GetMapping("/mypage")
    public String myPageForm(Principal principal, Model model) {
        List<StampCardWithGoalDTO> stampCardWithGoals = userService.getStampCardWithGoal(principal);
        UserDTO userAndStampCount = userService.getUserAndStampCount(principal);
        model.addAttribute("stampCardWithGoals", stampCardWithGoals);
        model.addAttribute("userAndStampCount", userAndStampCount);
        return "users/myPage";
    }

    // 내가 쓴 글, 댓글, 스크랩 등 목록
    @GetMapping("/{cate}/list")
    public String list(PageRequestDTO requestDTO, Model model, @PathVariable String cate, Principal principal) {
        model.addAttribute("result", boardService.getList(requestDTO, cate, principal));
        model.addAttribute("cate", cate);
        return "users/mainList";
    }

    // 관리자 포인트 충전
    @PostMapping("/charge")
    public ResponseEntity<String> charge(@RequestBody DonationDTO donationDTO) {

        String result = userService.chargeAdminPoint(donationDTO);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    // 사용자의 포인터를 매니저에게 전달하여 포인트 수집
    @PostMapping("/managerCollection")
    public ResponseEntity<String> collection(@RequestBody DonationDTO donationDTO) {

        String result = userService.collectionManagerPoint(donationDTO);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //마이페이지 수정
    @GetMapping("/info/modify")
    @PreAuthorize("isAuthenticated()")
    public String infoModify(Principal principal, Model model) {
        UserDTO userDTO = userService.getUserInfo(principal);
        model.addAttribute("myPageDTO", userDTO);
        log.error("myPageDTO={}",userDTO);
        return "users/infoModify";
    }

    //마이페이지 수정
    @PostMapping("/info/modify")
    @PreAuthorize("isAuthenticated()")
    public String infoModify(@Validated @ModelAttribute MyPageDTO myPageDTO, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {

        boolean checked = userService.validateDuplicateMyPage(myPageDTO, bindingResult, principal);

        //검증에 실패하면 다시 입력 폼으로
        if (checked) {
            log.info("errors={}", bindingResult);
            return "users/infoModify";
        }

        userService.userInfoModify(myPageDTO);
        redirectAttributes.addFlashAttribute("msg", principal.getName());

        return "redirect:/users/info/modify";
    }

    //마이페이지 PW 수정
    @GetMapping("/info/myPwModify")
    @PreAuthorize("isAuthenticated()")
    public String myPwModify(Model model) {
        model.addAttribute("myPwDTO", new MyPwDTO());
        return "users/infoModifyPw";
    }

    //마이페이지 PW 수정
    @PostMapping("/info/myPwModify")
    @PreAuthorize("isAuthenticated()")
    public String pwModify(@Validated @ModelAttribute MyPwDTO myPwDTO, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {

        log.error("myPwDTO={}", myPwDTO.getPassword());
        log.error("myPwDTO={}", myPwDTO.getNewPassword());
        log.error("myPwDTO={}", myPwDTO.getPasswordCheck());

        boolean checked = userService.validateDuplicatePw(myPwDTO, bindingResult, principal);

        // 검증에 실패하면 다시 입력 폼으로
        if (checked) {
            log.info("errors={}", bindingResult);
            return "users/infoModifyPw";
        }

        userService.userPwModify(myPwDTO, principal);
        redirectAttributes.addFlashAttribute("msg", principal.getName());

        return "redirect:/users/info/myPwModify";
    }

}
