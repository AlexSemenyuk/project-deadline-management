package org.itstep.projectdeadlinemanagement.command;

public record AssemblyTermCommand (
        Integer assemblyId,
        Integer number,
        Integer equipmentId,
        Integer operationTime
){
}



