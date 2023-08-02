package org.itstep.projectdeadlinemanagement.command;


import lombok.Getter;
import lombok.Setter;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChartDaysCommand {
    public static final String BLACK = "black";
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    @Getter @Setter
    private LocalDate dayNumber;

    // Процент загрузки оборудования в день
    @Getter @Setter
    private int planPerDay;

    @Getter @Setter
    private String dayColor;

    @Getter @Setter
    List<ProductionPlan> productionPlans = new ArrayList<>();

    public ChartDaysCommand(LocalDate dayNumber) {
        this.dayNumber = dayNumber;
    }

    @Override
    public String toString() {
        return "ChartDaysCommand{" +
                "dayNumber=" + dayNumber +
                ", planPerDay=" + planPerDay +
                ", dayColor='" + dayColor + '\'' +
                ", productionPlans=" + productionPlans +
                '}';
    }
}
