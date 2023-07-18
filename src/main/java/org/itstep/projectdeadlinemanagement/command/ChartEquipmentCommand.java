package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartEquipmentCommand {
    private String equipment;
    List<ChartDaysCommand> chartDaysCommands = new ArrayList<>();

    public ChartEquipmentCommand(String equipment) {
        this.equipment = equipment;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<ChartDaysCommand> getChartDaysCommands() {
        return chartDaysCommands;
    }

    public void setChartDaysCommands(List<ChartDaysCommand> chartDaysCommands) {
        this.chartDaysCommands = chartDaysCommands;
    }

    @Override
    public String toString() {
        return "ChartEquipmentCommand{" +
                "equipment='" + equipment + '\'' +
                ", chartDaysCommands=" + chartDaysCommands +
                '}';
    }
}
