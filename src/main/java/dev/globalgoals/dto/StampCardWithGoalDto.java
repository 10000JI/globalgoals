package dev.globalgoals.dto;

import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.StampCard;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StampCardWithGoalDto {
    private StampCard stampCard;
    private Goal goal;
}
