package dev.globalgoals.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.QBoard;
import dev.globalgoals.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void insetDummies() {
        Optional<User> userOptional = userRepository.findById("admin");

        userOptional.ifPresent(user -> {
            IntStream.rangeClosed(1, 300).forEach(i -> {
                Board board = Board.builder()
                        .title("Title ..." + i)
                        .content("Contents..." + i)
                        .user(user)
                        .build();
                boardRepository.save(board);
            });
        });
    }

    @Test
    public void updateTest() {
        Optional<Board> result = boardRepository.findById(300L);
        if (result.isPresent()) {
            Board board = result.get();
            board.changeTitle("Changed Title,,,");
            board.changeContent("Changed Content...");
            boardRepository.save(board);
        }
    }

    @Test
    public void testQuest1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        QBoard qBoard = QBoard.board;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = qBoard.title.contains("1");
        booleanBuilder.and(expression);
        Page<Board> result = boardRepository.findAll(booleanBuilder, pageable);
        result.forEach(boardbook -> {
            System.out.println(boardbook);
        });
    }
    @Test
    public void testQuest2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        QBoard qBoard = QBoard.board;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        BooleanExpression expression = qBoard.title.contains("1");
        BooleanExpression exAll = expression.or(qBoard.content.contains("1"));

        booleanBuilder.and(exAll);
        booleanBuilder.and(qBoard.id.gt(0L));

        Page<Board> result = boardRepository.findAll(booleanBuilder, pageable);

        result.forEach(boardbook -> {
            System.out.println(boardbook);
        });
    }
}