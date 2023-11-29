package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> getBoardImageByBoard(Board board);
    //SELECT *
    //FROM board_image
    //WHERE board_id = ?

    void deleteBySaveName(String saveName);
    //delete
    //from board_image
    //where save_name =?

    @Modifying
    @Query("delete from BoardImage bi where bi.board.id =:id ")
    void deleteByBno(@Param("id") Long id); //Board 삭제 시에 댓글들 삭제
}
