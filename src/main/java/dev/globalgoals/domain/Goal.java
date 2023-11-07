package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_title")
    private StampName goalTitle;

    @OneToMany(mappedBy = "goal")
    private List<GoalDetail> details = new ArrayList<>();
}
