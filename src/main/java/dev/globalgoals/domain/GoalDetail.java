package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class GoalDetail {

    @Id
    @GeneratedValue
    @Column(name = "goaldetail_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="goal_id")
    private Goal goal;

    @Column(name = "goaldetail_detail")
    private String goalDetail;
}
