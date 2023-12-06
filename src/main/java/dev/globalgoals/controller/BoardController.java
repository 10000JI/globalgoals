package dev.globalgoals.controller;

import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.CertifyDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.file.FileStore;
import dev.globalgoals.file.UploadFile;
import dev.globalgoals.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileStore fileStore;

    // 자유 게시판 & 실천 방법 등록 & 실천 등록 & 전체 글보기 리스트
    @GetMapping("/{cate}/list")
    public String list(PageRequestDTO requestDTO, Model model, @PathVariable String cate, Principal principal) {
        model.addAttribute("result", boardService.getList(requestDTO, cate, principal));
        model.addAttribute("cate", cate);
        if (cate.equals("main") || cate.equals("popularity") || cate.equals("mine") || cate.equals("comment")) {
            return "board/mainList";
        }
        return "board/list";
    }

    // 자유 게시판 등록 페이지
    @GetMapping("/{cate}/register")
    public String register(Model model ,@PathVariable String cate, BoardDTO dto) {
        log.info("register get....");
        model.addAttribute("cate", cate);
        model.addAttribute("dto", dto);
        return "board/register";
    }

    // 자유 게시판 등록
    @PostMapping("/{cate}/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes) throws IOException {
        log.error("category={}", dto.getCategory());
        //새로 추가된 엔티티의 번호
        Long id = boardService.register(dto);

        redirectAttributes.addFlashAttribute("msg", id);
        return "redirect:/board/{cate}/list";
    }

    //자유 게시판 조회
    @GetMapping("/{cate}/read")
    public String read(Long id, @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO, Model model, @PathVariable String cate) {

        log.info("id" + id);

        BoardDTO dto = boardService.get(id);

        List<UploadFile> images = boardService.getImage(id);

        model.addAttribute("cate", cate);
        model.addAttribute("dto", dto);
        model.addAttribute("images", images);

        return "board/read";
    }
    //자유 게시판 조회
    @GetMapping("/{cate}/modify")
    public String modify(Long id, @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO, Model model, @PathVariable String cate) {

        log.info("id" + id);

        BoardDTO dto = boardService.get(id);

        List<UploadFile> images = boardService.getImage(id);

        model.addAttribute("cate", cate);
        model.addAttribute("dto", dto);
        model.addAttribute("images", images);

        return "board/modify";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        //"file:C:/SpringStudy/file/8f9764ed-7bfb-40a2-80a4-b1c3162ba5ef.jpg"
        return new UrlResource("file:" + fileStore.getFullPath(filename)); //경로에 있는 파일에 접근하여 Stream으로 반환
    }

    @PostMapping("/{cate}/modify")
    public String modify(BoardDTO dto,String[] storeFileNames, @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes,  @PathVariable String cate) throws IOException {
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

        return "redirect:/board/{cate}/read";
    }

    @PostMapping("/{cate}/remove")
    public String remove(Long id, RedirectAttributes redirectAttributes, @PathVariable String cate) {
        log.info("id: " + id);
        boardService.removeWithComments(id);

        redirectAttributes.addAttribute("msg", id);

        return "redirect:/board/{cate}/list";
    }

    @PostMapping("/scrap/{bno}")
    public ResponseEntity<String> remove(@PathVariable("bno") Long bno, Principal principal) {

        log.info("rno:" + bno );

        String result = boardService.saveScrap(bno, principal);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/certify")
    public ResponseEntity<String> remove(@RequestBody CertifyDTO certifyDTO) {

        String result = boardService.saveCertify(certifyDTO);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
