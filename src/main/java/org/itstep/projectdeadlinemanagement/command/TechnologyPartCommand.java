package org.itstep.projectdeadlinemanagement.command;

public record TechnologyPartCommand(
        Integer partId,
        Integer number,
        Integer equipmentId,
//        String name,
        Integer operationTime
) {
}



