package org.itstep.projectdeadlinemanagement.command;

public record TaskCommand(
        Integer projectNumber,
        Integer equipmentId,
        Integer partNumber,
        String partName,
        Integer termNumber,
        Integer operationTime,
        Integer lotNumber,
        Integer taskConditionId
) {
}



