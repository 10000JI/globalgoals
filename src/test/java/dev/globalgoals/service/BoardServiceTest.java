package dev.globalgoals.service;

import dev.globalgoals.dto.BoardDto;
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
}