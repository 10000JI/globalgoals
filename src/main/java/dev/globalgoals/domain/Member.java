package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_pw")
    private String password;

    @Column(name = "member_phone")
    private String phone;

    @Column(name = "member_email")
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleName rolename;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();
}
