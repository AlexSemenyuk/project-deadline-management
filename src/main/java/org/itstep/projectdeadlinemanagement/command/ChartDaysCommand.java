package org.itstep.projectdeadlinemanagement.command;

import org.itstep.projectdeadlinemanagement.model.ProductionPlan;

import java.util.ArrayList;
import java.util.List;

public class ChartDaysCommand {
    private int dayNumber;
    private int planPerDay;
    List<ProductionPlan> productionPlans = new ArrayList<>();

    public ChartDaysCommand(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getPlanPerDay() {
        return planPerDay;
    }

    public void setPlanPerDay(int planPerDay) {
        this.planPerDay = planPerDay;
    }

    public List<ProductionPlan> getProductionPlans() {
        return productionPlans;
    }

    public void setProductionPlans(List<ProductionPlan> productionPlans) {
        this.productionPlans = productionPlans;
    }

    @Override
    public String toString() {
        return "ChartDaysCommand{" +
                "dayNumber=" + dayNumber +
                ", planPerDay=" + planPerDay +
                ", productionPlans=" + productionPlans +
                '}';
    }
}
