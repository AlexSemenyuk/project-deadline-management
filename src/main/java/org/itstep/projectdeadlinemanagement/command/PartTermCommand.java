package org.itstep.projectdeadlinemanagement.command;

public record PartTermCommand(
        Integer partId,
        Integer number,
        Integer equipmentId,
//        String name,
        Integer operationTime
) {
}



