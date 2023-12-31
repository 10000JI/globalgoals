package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    BoardService service;

    @Test
    public void testRegister() throws Exception{

        BoardDTO boardDto = BoardDTO.builder()
                .title("Sample Title...")
                .content("Sample content...")
                .writer("admin")
                .build();
        System.out.println(service.register(boardDto));
    }

}