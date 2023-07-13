package org.itstep.projectdeadlinemanagement.command;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record ProjectCommand(
        Integer number,
        List<Integer> projectListsIds,
        Integer customerId,
        LocalDateTime start,
        LocalDateTime deadline,
        Integer projectConditionId
) {
}


