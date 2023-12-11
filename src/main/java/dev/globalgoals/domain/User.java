package dev.globalgoals.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @Id
    @Column(name = "user_id", length = 50, unique = true)
    private String id;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "countDonation")
    @ColumnDefault("0")
    private Long countDonation; //기부 횟수

    @ColumnDefault("0")
    private Long donatedPoints; //목표를 다 채우면 포인트 0 -> 1700 변경

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "board_scrap",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "board_id", referencedColumnName = "board_id")})
    private Set<Board> scrap;

    public void plusDonatedPoints(Long donatedPoints) {
        this.donatedPoints += donatedPoints;
    }

    public void changeDonatedPoint(Long donatedPoints) {
        this.donatedPoints = donatedPoints;
    }
    public void minusDonatedPoint(Long donatedPoints) {
        this.donatedPoints -= donatedPoints;
    }

    public void plusCountDonation() {
        countDonation++;
    }

    public void changeName(String name){
        this.name = name;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePw(String password) {
        this.password = password;
    }


//    @OneToMany(mappedBy = "user")
//    private List<StampCard> stampCards = new ArrayList<>();


}
