package org.itstep.projectdeadlinemanagement.command;

import java.time.LocalDateTime;
import java.util.List;

public record ProductionPlanTermCommand(
        LocalDateTime currentStart,
        Integer taskConditionId
) {
}
