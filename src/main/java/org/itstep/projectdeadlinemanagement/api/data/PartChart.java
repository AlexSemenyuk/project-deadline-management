package org.itstep.projectdeadlinemanagement.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PartChart {
    private int projectNumber;
    private int partNumber;
    private String partName;
    List<Integer> termNumberList;
    List<TermHours> termHoursList;
}
