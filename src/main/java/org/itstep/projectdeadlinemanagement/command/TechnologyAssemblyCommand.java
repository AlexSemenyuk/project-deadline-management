package org.itstep.projectdeadlinemanagement.command;

public record TechnologyAssemblyCommand(
        Integer assemblyId,
        Integer number,
        Integer equipmentId,
        Integer operationTime
){
}



