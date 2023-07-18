package org.itstep.projectdeadlinemanagement.command;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.TaskCondition;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChartPlanCommand {
    private int id;
    private int day;
    private int number;
    private LocalDateTime currentStart;
    private Integer projectNumber;
    private String equipment;
    private String part;
    private Integer termNumber;
    private Integer operationTime;
    private Integer lotNumber;
    private LocalDateTime projectStart;
    private String condition;



}
