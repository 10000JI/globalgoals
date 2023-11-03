package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="cate_id")
    private BoardCategory boardCategory;

    @ManyToOne
    @JoinColumn(name="member_id")
    private User member;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @Column(name = "board_boardCreatd")
    private LocalDateTime boardDate;

    @Column(name = "board_hit")
    private Long hit;
}
