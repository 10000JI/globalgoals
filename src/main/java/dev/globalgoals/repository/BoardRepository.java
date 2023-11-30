package dev.globalgoals.repository;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardCategory;
import dev.globalgoals.repository.search.SearchBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {

    @Query("select b, w from Board b left join b.user w where b.id=:id")
    Object getBoardWithUser(@Param("id") Long id);

    @Query("select b, bc from Board b left join BoardComment bc on bc.board = b where b.id = :id")
    List<Object[]> getBoardWithComment(@Param("id") Long id);

    @Query(value = " select b, u, count(bc) " +
            " from Board b " +
            " left join b.user u " +
            " left join BoardComment bc on bc.board = b" +
            " group by b", countQuery = "select count(b) from Board b")
    Page<Object[]> getBoardWithCommentCount(Pageable pageable);

    @Query("SELECT b, u, count(bc), bcy " +
            " FROM Board b LEFT JOIN b.user u " +
            "LEFT JOIN b.boardCategory bcy " +
            " LEFT OUTER JOIN BoardComment bc ON bc.board = b" +
            " WHERE b.id = :id")
    Object getBoardById(@Param("id") Long id);

//    Board getBoardByBoardCategory_Id(Long cateNum);
//    //SELECT *
//    //FROM board
//    //WHERE cate_name = ?

}
