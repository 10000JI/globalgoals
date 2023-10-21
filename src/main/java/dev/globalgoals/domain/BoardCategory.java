package dev.globalgoals.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BoardCategory {
    @Id
    @GeneratedValue
    @Column(name = "cate_no")
    private Long id;

    @Column(name = "cate_name")
    private String categoryName;
}
