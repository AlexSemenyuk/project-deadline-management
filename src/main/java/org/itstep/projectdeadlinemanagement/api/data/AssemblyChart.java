package org.itstep.projectdeadlinemanagement.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssemblyChart {
    private int projectNumber;
    private int assemblyNumber;
    private String assemblyName;
    List<Integer> termNumberList;
    List<TermHours> termHoursList;
}


