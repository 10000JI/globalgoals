package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardCategory;
import dev.globalgoals.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
    BoardCategory getBoardCategoriesByCategoryName(String cateName);
    //SELECT *
    //FROM board_category
    //WHERE cate_name = ?
}
