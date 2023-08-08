package org.itstep.projectdeadlinemanagement.api.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TermDate {
    private LocalDate start;
    private LocalDate deadline;
}
