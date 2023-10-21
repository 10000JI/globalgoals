package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StampCard {

    @Id
    @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name = "card_check")
    private boolean check;

    @Enumerated(EnumType.STRING)
    private StampName stampName;
}
