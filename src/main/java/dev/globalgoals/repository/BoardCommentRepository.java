package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    @Modifying
    @Query("delete from BoardComment r where r.board.id =:id ")
    void deleteByBno(@Param("id") Long id);

    List<BoardComment> getBoardCommentByBoardOrderById(Board board);
}
