package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardComment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardCommentRepositoryTest {

    @Autowired
    private BoardCommentRepository boardCommentRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Test
    public void insertBoardComment(){
        int numberOfComments = 321;
        for (int i = 0; i < numberOfComments; i++) {
            long boardId = (long) (Math.random() * 100) * 3 + 1;
            insertBoardComment(boardId);
        }
    }

    private void insertBoardComment(long boardId) {
        List<Board> boards = boardRepository.findAll();
        boards.forEach(board ->{
            if (boardId == board.getId()) {
                BoardComment boardComment = BoardComment.builder()
                        .comments("Reply.......")
                        .board(boardRepository.findById(board.getId()).orElseThrow())
                        .writer("alswl3359")
                        .build();
                boardCommentRepository.save(boardComment);
            }
        });
    }

}