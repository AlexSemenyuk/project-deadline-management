package org.itstep.projectdeadlinemanagement.command;

import java.util.List;
public record DivisionCommand(
        Integer number,
        String name,
        Integer divisionTypeId
) {
}



