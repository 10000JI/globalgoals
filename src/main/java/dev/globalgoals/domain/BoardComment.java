package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_num")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    private String comments;

    private String writer;
}
