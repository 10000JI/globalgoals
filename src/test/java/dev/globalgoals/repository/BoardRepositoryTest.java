package dev.globalgoals.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.Board;
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
        List<User> admin = userRepository.findById("admin");

        System.out.println(admin.get(0).getName());
        if (!admin.isEmpty()) {
            User user = admin.get(0);
            IntStream.rangeClosed(1, 300).forEach(i ->{
                Board board = Board.builder()
                        .title("Title ..." + i)
                        .content("Contents..." + i)
                        .member(user)
                        .build();
                boardRepository.save(board);
            });
        }

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
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

    }
}