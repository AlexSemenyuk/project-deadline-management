package org.itstep.projectdeadlinemanagement.api.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Term {
    private LocalDate start;
    private LocalDate deadline;
}
