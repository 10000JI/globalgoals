package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    @Id
    @GeneratedValue
    @Column(name = "goal_id")
    private Long id;

    @Column(name = "goal_title")
    private String goalTitle;

}
