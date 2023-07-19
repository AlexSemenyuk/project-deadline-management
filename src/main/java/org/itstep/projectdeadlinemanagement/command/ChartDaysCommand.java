package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.List;

public class ChartDaysCommand {
    private int dayNumber;
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
                ", chartPlanCommandList=" + chartPlanCommandList +
                '}';
    }
}
