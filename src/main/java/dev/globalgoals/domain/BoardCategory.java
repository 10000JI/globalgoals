package dev.globalgoals.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardCategory {
    @Id
    @GeneratedValue
    @Column(name = "cate_id")
    private Long id;

    @Column(name = "cate_name")
    private String categoryName;
}
