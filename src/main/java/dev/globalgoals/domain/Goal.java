package dev.globalgoals.domain;

import lombok.*;

import jakarta.persistence.*;
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
    private Long goalId;

    @Column(name = "goal_title")
    private String goalTitle;

//    @OneToMany(mappedBy = "goal")
//    private List<StampCard> stampCards = new ArrayList<>();
}
