package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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
