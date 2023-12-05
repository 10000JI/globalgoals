package dev.globalgoals.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampCardWithGoalDTO {
    private Long stampId;
    private String userName;
    private Integer checkNum;
    private Long goalId;
    private String goalTitle;
}
