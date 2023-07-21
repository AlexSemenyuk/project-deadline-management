package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.List;

public class ChartDaysCommand {
    private int dayNumber;
    private int planPerDay;
    List<ChartPlanCommand> chartPlanCommandList = new ArrayList<>();

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

    public List<ChartPlanCommand> getChartPlanCommandList() {
        return chartPlanCommandList;
    }

    public void setChartPlanCommandList(List<ChartPlanCommand> chartPlanCommandList) {
        this.chartPlanCommandList = chartPlanCommandList;
    }

    @Override
    public String toString() {
        return "ChartDaysCommand{" +
                "dayNumber=" + dayNumber +
                ", planPerDay=" + planPerDay +
                ", chartPlanCommandList=" + chartPlanCommandList +
                '}';
    }
}
