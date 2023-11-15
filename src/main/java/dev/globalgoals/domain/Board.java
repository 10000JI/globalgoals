package dev.globalgoals.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class Board extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="cate_id")
//    private BoardCategory boardCategory;

    @ManyToOne
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
    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeContent(String content) {
        this.content = content;
    }
}
