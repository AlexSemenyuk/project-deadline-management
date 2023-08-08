package org.itstep.projectdeadlinemanagement.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermHours {
    private int start;
    private int deadline;
}

