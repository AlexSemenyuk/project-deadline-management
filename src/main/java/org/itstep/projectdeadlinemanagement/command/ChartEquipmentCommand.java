package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartEquipmentCommand {
    private int equipmentId;
    private String equipment;
    private int factHoursPerMonth;
    // Процент загрузки оборудования в месяц
    private int planFromTheTotal;
    // Процент выполненных заданий в месяц (сумма OperationTime (taskCondition.name = "Ок") к общей сумме OperationTime в месяц)
    private int doneFromPlan;
    // Планируемый процент выполненных заданий в месяц на текущую дату (сумма OperationTime до текущей даты к общей сумме OperationTime в месяц )
    private int planForTheCurrentDate;
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

    public int getPlanFromTheTotal() {
        return planFromTheTotal;
    }

    public void setPlanFromTheTotal(int planFromTheTotal) {
        this.planFromTheTotal = planFromTheTotal;
    }

    public int getDoneFromPlan() {
        return doneFromPlan;
    }

    public void setDoneFromPlan(int doneFromPlan) {
        this.doneFromPlan = doneFromPlan;
    }

    public int getPlanForTheCurrentDate() {
        return planForTheCurrentDate;
    }

    public void setPlanForTheCurrentDate(int planForTheCurrentDate) {
        this.planForTheCurrentDate = planForTheCurrentDate;
    }

    public int getFactHoursPerMonth() {
        return factHoursPerMonth;
    }

    public void setFactHoursPerMonth(int factHoursPerMonth) {
        this.factHoursPerMonth = factHoursPerMonth;
    }

    @Override
    public String toString() {
        return "ChartEquipmentCommand{" +
                "equipmentId=" + equipmentId +
                ", equipment='" + equipment + '\'' +
                ", factHoursPerMonth=" + factHoursPerMonth +
                ", planFromTheTotal=" + planFromTheTotal +
                ", doneFromPlan=" + doneFromPlan +
                ", planForTheCurrentDate=" + planForTheCurrentDate +
                ", chartDaysCommands=" + chartDaysCommands +
                '}';
    }
}
