package dev.globalgoals.controller;

import dev.globalgoals.dto.BoardDto;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 자유 게시판 리스트
    @GetMapping("/free/list")
    public void mainList(PageRequestDTO requestDTO, Model model) {
        model.addAttribute("result", boardService.getList(requestDTO));
    }

    // 자유 게시판 등록 페이지
    @GetMapping("/free/register")
    public void register() {
        log.info("register get....");
    }
    // 자유 게시판 등록
    @PostMapping ("/free/register")
    public String registerPost(BoardDto dto, RedirectAttributes redirectAttributes) {
        log.info("dto...." + dto);

        //새로 추가된 엔티티의 번호
        Long gno = boardService.register(dto);

        redirectAttributes.addFlashAttribute("msg",gno);
        return "redirect:/board/free/list";
    }
}
