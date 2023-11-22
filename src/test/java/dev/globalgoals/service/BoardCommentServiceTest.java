package dev.globalgoals.service;

import dev.globalgoals.dto.BoardCommentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardCommentServiceTest {

    @Autowired
    private BoardCommentService commentService;


    @Test
    public void testGetList() {
        Long id = 100L;

        List<BoardCommentDTO> commentDTOList = commentService.getList(id);

        commentDTOList.forEach(commentDTO -> System.out.println(commentDTO));
    }

}