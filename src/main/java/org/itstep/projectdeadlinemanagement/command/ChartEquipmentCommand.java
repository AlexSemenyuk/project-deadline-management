package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartEquipmentCommand {
    private int equipmentId;
    private String equipment;
    List<ChartDaysCommand> chartDaysCommands = new ArrayList<>();

    public ChartEquipmentCommand(int equipmentId, String equipment) {
        this.equipmentId = equipmentId;
        this.equipment = equipment;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
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
                "equipmentId=" + equipmentId +
                ", equipment='" + equipment + '\'' +
                ", chartDaysCommands=" + chartDaysCommands +
                '}';
    }
}
