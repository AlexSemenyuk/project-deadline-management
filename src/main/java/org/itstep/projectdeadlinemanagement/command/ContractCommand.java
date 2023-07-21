package org.itstep.projectdeadlinemanagement.command;

import java.time.LocalDateTime;

public record ContractCommand (
        String number,
        String name,
        LocalDateTime start,
        LocalDateTime deadline,
        Integer contractTypeId
) {
}


