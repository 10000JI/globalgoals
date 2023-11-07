package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardComment {

    @Id
    @GeneratedValue
    @Column(name = "comment_num")
    private Long id;

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;

    private String comments;

    private LocalDateTime regDate;

    private String writer;
}
