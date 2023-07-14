package org.itstep.projectdeadlinemanagement.command;

public record TermPartCommand(
        Integer partId,
        Integer number,
        Integer equipmentId,
//        String name,
        Integer operationTime
) {
}



