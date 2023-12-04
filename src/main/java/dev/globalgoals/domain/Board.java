package dev.globalgoals.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@ToString(exclude = "user")
public class Board extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cate_id")
    private BoardCategory boardCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name = "board_title", length = 50, nullable = false)
    private String title;

    @Column(name = "board_content", length = 4000, nullable = false)
    private String content;

    @Column(name = "board_hit")
    @ColumnDefault("0")
    private Long hit;

    // 엔티티(도메인)은 불변으로 설계하는 것이 좋지만, 필요한 부분은 수정할 수 있도록 메서드를 만드는 건 좋은 예시이다.
    // 불변으로 설계하게 되면, 불필요한 로직이 너무 많이 들어가기 때문에 일부 데이터를 수정하고 싶을 땐 메서드를 만들자 -> 대신 의미있는 메서드 이름
    // 특히 수정 시에는 다음과 같이 "변경 감지 기능"을 사용하여, 준영속 엔티티를 (1) 조회하고 (2) 자바 코드로 값을 변경하여 (3) save하는 방향으로 사용하자.
    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeContent(String content) {
        this.content = content;
    }

    public void changeBoardCategory(BoardCategory boardCategory){
        this.boardCategory = boardCategory;
    }

    public void plusHit() {
        hit++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; //객체가 자기 자신인지 확인
        if (o == null || getClass() != o.getClass()) return false; //객체가 null 이거나 클래스가 다르다면 두 객체는 동일하지 않음
        Board board = (Board) o; // 형변환을 통해 비교할 객체로 변환
        return id != null && id.equals(board.id); /// id가 null이 아니고, 두 객체의 id가 같다면 두 객체는 동일
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); //클래스의 hashCode를 반환합니다.
    }

}
