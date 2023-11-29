package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
