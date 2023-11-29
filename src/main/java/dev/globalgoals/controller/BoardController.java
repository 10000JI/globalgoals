package dev.globalgoals.controller;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardImage;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.file.FileStore;
import dev.globalgoals.file.UploadFile;
import dev.globalgoals.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileStore fileStore;
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
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes) throws IOException {
        //새로 추가된 엔티티의 번호
        Long id = boardService.register(dto);

        redirectAttributes.addFlashAttribute("msg", id);
        return "redirect:/board/free/list";
    }

    //자유 게시판 조회
    @GetMapping({"/free/read", "/free/modify"})
    public void read(Long id, @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO, Model model) {

        log.info("id" + id);

        BoardDTO dto = boardService.get(id);

        List<UploadFile> images = boardService.getImage(id);

        model.addAttribute("dto", dto);
        model.addAttribute("images", images);
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        //"file:C:/SpringStudy/file/8f9764ed-7bfb-40a2-80a4-b1c3162ba5ef.jpg"
        return new UrlResource("file:" + fileStore.getFullPath(filename)); //경로에 있는 파일에 접근하여 Stream으로 반환
    }

    @PostMapping("/free/modify")
    public String modify(BoardDTO dto,String[] storeFileNames, @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes) throws IOException {
        log.info("post modify...............");
        log.info("dto: " + dto);
        if (storeFileNames != null) {
            for (String storeFileName : storeFileNames) {
                log.info("storeFileName: " + storeFileName);
            }
        }

        boardService.modify(dto,storeFileNames);

        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("id", dto.getId());

        return "redirect:/board/free/read";
    }

    @PostMapping("/free/remove")
    public String remove(Long id, RedirectAttributes redirectAttributes) {
        log.info("id: " + id);
        boardService.removeWithComments(id);

        redirectAttributes.addAttribute("msg", id);

        return "redirect:/board/free/list";
    }
}
