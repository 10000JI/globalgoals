package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StampCard {

    @Id
    @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private User member;

    @Column(name = "card_check")
    private boolean check;

    @Enumerated(EnumType.STRING)
    private StampName stampName;
}
