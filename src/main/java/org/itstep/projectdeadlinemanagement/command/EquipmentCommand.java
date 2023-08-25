package org.itstep.projectdeadlinemanagement.command;

public record EquipmentCommand(
        Integer number,
        String name,
        Integer divisionId,
        Integer equipmentTypeId
) {
}
