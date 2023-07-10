package org.itstep.projectdeadlinemanagement.command;

import java.util.List;

public record TermCommand(
        Integer partId,
        Integer number,
        Integer equipmentId,
//        String name,
        Integer operationTime
) {
}



