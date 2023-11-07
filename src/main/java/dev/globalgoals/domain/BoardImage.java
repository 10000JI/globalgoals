package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;

    private String oriName;

    private String saveName;
}
