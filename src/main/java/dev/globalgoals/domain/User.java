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
    private Long countDonation;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;


    @ManyToMany
    @JoinTable(
            name = "board_scrap",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "board_id", referencedColumnName = "board_id")})
    private Set<Board> scrap;

//    @OneToMany(mappedBy = "user")
//    private List<StampCard> stampCards = new ArrayList<>();


}
