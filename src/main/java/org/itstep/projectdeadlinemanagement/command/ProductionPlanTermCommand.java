package org.itstep.projectdeadlinemanagement.command;

import java.time.LocalDateTime;

public record ProductionPlanTermCommand(
        LocalDateTime currentStart,
        Integer taskConditionId
) {
}
