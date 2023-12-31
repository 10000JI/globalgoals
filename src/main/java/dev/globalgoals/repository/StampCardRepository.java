package dev.globalgoals.repository;

import dev.globalgoals.domain.StampCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StampCardRepository extends JpaRepository<StampCard, String> {
    StampCard findByUser_IdAndGoal_GoalId (String userId, Long goalId); // 인증하려고 하는 게시판 유저의 stamp_card 정보 가져오기
    //select * from stamp_card sc where user_id ='alswl3359' and goal_id=1; (단일 객체만 반환)

    // JPQL을 사용하여 checkNum이 1이고 user_id가 특정 값인 StampCard의 개수와 사용자 정보를 조회하는 메서드
    @Query("SELECT COUNT(sc) FROM StampCard sc LEFT JOIN sc.user u WHERE sc.checkNum = 1 AND sc.user.id = :userId")
    Long countByCheckNum(@Param("userId")String userId);

    List<StampCard> getStampCardByUser_Id(String userId);
    //select * from stamp_card where user_id="dhk9309" (객체를 컬렉션으로 반환)

    void deleteByUserId(String userId);
    //delete from stamp_card where user_id=?
}
