package org.itstep.projectdeadlinemanagement.command;

import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Date;

public record ProjectCommand(
        Integer number,
        String name,
        Integer customerId,
        LocalDateTime start,
        LocalDateTime deadline
) {
}


