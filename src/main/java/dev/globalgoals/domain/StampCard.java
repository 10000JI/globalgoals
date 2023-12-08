package dev.globalgoals.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StampCard {

    @Id
    @GeneratedValue
    @Column(name = "stamp_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(name = "check_num")
    private Integer checkNum;

    public void plusCheckNum() {
        checkNum = 1;
    }

    public void minusCheckNum() {
        checkNum = 0;
    }

}
