package org.itstep.projectdeadlinemanagement.command;

public record AssemblyListCommand (
        Integer assemblyId,
        Integer amount
) {
}

