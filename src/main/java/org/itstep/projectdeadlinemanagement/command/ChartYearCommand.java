package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.List;

public class ChartYearCommand {

    private int equipmentId;
    private String equipment;

    // П(г) - Плановая загрузка оборудования в год (часы), т.е кол-во рабочих часов в год
    private int planHoursPerYear;

    // Ф(г) - Фактическая загрузка оборудования в год (часы)
    private int factHoursPerYear;

    // Фактичне завантаження обладнання на місяць (відсоток)
    private int factPercentsPerYear;

    // ПВЗ(%) - План выполненных заданий на текущую дату (процент)
    private int planForPlanDoneOnTheCurrentDatePerYear;

    // ФВЗ(%) - Факт выполненных заданий относительно их планового количества (проценты)
    // Т.е. сумма OperationTime (taskCondition.name = "Ок") к общей сумме OperationTime в месяц
    private int factForPlanDonePerYear;

    List<ChartMonthCommand> chartMonthCommands = new ArrayList<>();

    public ChartYearCommand(int equipmentId, String equipment) {
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

    public int getPlanHoursPerYear() {
        return planHoursPerYear;
    }

    public void setPlanHoursPerYear(int planHoursPerYear) {
        this.planHoursPerYear = planHoursPerYear;
    }

    public int getFactHoursPerYear() {
        return factHoursPerYear;
    }

    public void setFactHoursPerYear(int factHoursPerYear) {
        this.factHoursPerYear = factHoursPerYear;
    }

    public int getFactPercentsPerYear() {
        return factPercentsPerYear;
    }

    public void setFactPercentsPerYear(int factPercentsPerYear) {
        this.factPercentsPerYear = factPercentsPerYear;
    }

    public int getPlanForPlanDoneOnTheCurrentDatePerYear() {
        return planForPlanDoneOnTheCurrentDatePerYear;
    }

    public void setPlanForPlanDoneOnTheCurrentDatePerYear(int planForPlanDoneOnTheCurrentDatePerYear) {
        this.planForPlanDoneOnTheCurrentDatePerYear = planForPlanDoneOnTheCurrentDatePerYear;
    }

    public int getFactForPlanDonePerYear() {
        return factForPlanDonePerYear;
    }

    public void setFactForPlanDonePerYear(int factForPlanDonePerYear) {
        this.factForPlanDonePerYear = factForPlanDonePerYear;
    }

    public List<ChartMonthCommand> getChartMonthCommands() {
        return chartMonthCommands;
    }

    public void setChartMonthCommands(List<ChartMonthCommand> chartMonthCommands) {
        this.chartMonthCommands = chartMonthCommands;
    }

    @Override
    public String toString() {
        return "ChartYearCommand{" +
                "equipmentId=" + equipmentId +
                ", equipment='" + equipment +
                ", planHoursPerYear=" + planHoursPerYear +
                ", factHoursPerYear=" + factHoursPerYear +
                ", factPercentsPerYear=" + factPercentsPerYear +
                ", planForPlanDoneOnTheCurrentDatePerYear=" + planForPlanDoneOnTheCurrentDatePerYear +
                ", factForPlanDonePerYear=" + factForPlanDonePerYear +
                ", chartMonthCommands=" + chartMonthCommands +
                '}';
    }
}


