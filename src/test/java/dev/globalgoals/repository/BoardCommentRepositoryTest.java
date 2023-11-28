package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardComment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    @Test
    public void insertBoardComment_i(){
        int numberOfComments = 300;
        for (int i = 1; i < numberOfComments; i++) {
            if (i % 2 == 0) {
                insertBoardComment_o(i);
            }
        }
    }

    private void insertBoardComment_o(long numberOfComments) {
        Optional<Board> boardOptional = boardRepository.findById(numberOfComments);
        if (boardOptional.isPresent()) {
            BoardComment boardComment = BoardComment.builder()
                    .comments("Reply.......")
                    .board(boardOptional.get()) // Optional에서 값을 가져옴
                    .writer("alswl3359")
                    .build();
            boardCommentRepository.save(boardComment);
        } else {
            // 원하는 작업 수행 또는 예외를 던지거나 로깅할 수 있음
            System.out.println("게시판을 찾을 수 없습니다: " + numberOfComments);
        }
    }


    @Test
    public void testListByBoard() {

        List<BoardComment> replyList = boardCommentRepository.getBoardCommentByBoardOrderById(
                Board.builder().id(100L).build());

        replyList.forEach(reply -> System.out.println(reply));
    }


}