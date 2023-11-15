package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.dto.BoardDto;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    BoardService service;

    @Test
    public void testRegister() throws Exception{

        BoardDto boardDto = BoardDto.builder()
                .title("Sample Title...")
                .content("Sample content...")
                .writer("admin")
                .build();
        System.out.println(service.register(boardDto));
    }

    @Test
    public void testList() throws Exception{
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();
        PageResultDTO<BoardDto, Board> resultDTO = service.getList(pageRequestDTO);
        for (BoardDto boardDto : resultDTO.getDtoList()) {
            System.out.println("boardDto = " + boardDto);
        }
    }
}