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
    void deleteByBno(@Param("id") Long id); //Board 삭제 시에 댓글들 삭제

    List<BoardComment> getBoardCommentByBoardOrderById(Board board); //게시물로 댓글 리스트 가져오기
    //SELECT *
    //FROM reply
    //WHERE board_id = ?
    //ORDER BY rno;

    List<BoardComment> getBoardCommentByWriter(String writer);
}
