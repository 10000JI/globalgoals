package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

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
}