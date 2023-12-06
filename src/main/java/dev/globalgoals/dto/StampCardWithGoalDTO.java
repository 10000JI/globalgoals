package dev.globalgoals.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampCardWithGoalDTO {
    //StampCard
    private Long stampId;
    private String userId;
    private Integer checkNum;

    //Goal
    private Long goalId;
    private String goalTitle;
}
