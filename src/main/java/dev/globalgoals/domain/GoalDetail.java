package dev.globalgoals.domain;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDetail {

    @Id
    @GeneratedValue
    @Column(name = "goaldetail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="goal_id")
    private Goal goal;

    @Column(name = "goaldetail_detail")
    private String goalDetail;
}
