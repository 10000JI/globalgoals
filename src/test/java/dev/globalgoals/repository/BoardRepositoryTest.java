package dev.globalgoals.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardCategory;
import dev.globalgoals.domain.QBoard;
import dev.globalgoals.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        Optional<User> userOptional = userRepository.findById("minji");

        userOptional.ifPresent(user -> {
            IntStream.rangeClosed(1, 100).forEach(i -> {
                Board board = Board.builder()
                        .title("Title ..." + i)
                        .content("Contents..." + i)
                        .user(user)
                        .boardCategory(BoardCategory.builder().id(2L).build())
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


//    @Test
//    public void testReadWithUser(){
//        //given
//        Object result = boardRepository.getBoardWithUser(100L);
//
//        //when
//        Object[] arr = (Object[]) result;
//
//        //then
//        System.out.println(Arrays.toString(arr));
//    }

    @Test
    public void testGetBoardWithComment(){
        //given
        List<Object[]> result = boardRepository.getBoardWithComment(103L);

        //when
        for (Object[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }

    @Test
    @Transactional
    public void testWithCommentCount() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Object[]> result = boardRepository.getBoardWithCommentCount(pageable);
        result.get().forEach(row -> {
            Object[] arr = (Object[])row;
            System.out.println(Arrays.toString(arr));
        });
    }


    @Test
    @Transactional
    public void testRead3() {

        Object result = boardRepository.getBoardById(103L);

        Object[] arr = (Object[])result;

        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testSearch1() {

        boardRepository.search1();

    }

//    @Test
//    @Transactional
//    public void testSearchPage() {
//
//        Pageable pageable =
//                PageRequest.of(0,10,
//                        Sort.by("id").descending()
//                                .and(Sort.by("title").ascending()));
//
//        Page<Object[]> result = boardRepository.searchPage("t", "1", pageable);
//
//    }


}