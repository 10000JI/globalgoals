package dev.globalgoals.controller;

import dev.globalgoals.dto.BoardCommentDTO;
import dev.globalgoals.service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/boardComment")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService commentService;

    @GetMapping(value = "/board/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BoardCommentDTO>> getListByBoard(@PathVariable("id") Long id ){

        log.info("id: " + id);

        return new ResponseEntity<>( commentService.getList(id), HttpStatus.OK);

    }
}
