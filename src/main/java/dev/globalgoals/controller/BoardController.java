package dev.globalgoals.controller;

import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    @PostMapping("/free/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes) {
        log.info("dto...." + dto);

        //새로 추가된 엔티티의 번호
        Long gno = boardService.register(dto);

        redirectAttributes.addFlashAttribute("msg", gno);
        return "redirect:/board/free/list";
    }

    //자유 게시판 디테일
    @GetMapping({"/free/read", "/free/modify"})
    public void read(Long id, @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO, Model model) {

        log.info("id" + id);

        BoardDTO dto = boardService.read(id);

        model.addAttribute("dto", dto);
    }

    @PostMapping("/free/modify")
    public String modify(BoardDTO dto, @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes) {
        log.info("post modify...............");
        log.info("dto: " + dto);

        boardService.modify(dto);

        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("id", dto.getId());

        return "redirect:/board/free/read";
    }

    @PostMapping("/free/remove")
    public String remove(Long id, RedirectAttributes redirectAttributes) {
        log.info("id: " + id);
        boardService.remove(id);

        redirectAttributes.addAttribute("msg", id);

        return "redirect:/board/free/list";
    }
}
