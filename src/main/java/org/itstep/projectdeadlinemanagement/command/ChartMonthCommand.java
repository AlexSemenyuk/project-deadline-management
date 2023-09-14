package org.itstep.projectdeadlinemanagement.command;

import java.util.ArrayList;
import java.util.List;

public class ChartMonthCommand {
    private int monthNumber;

    // П(г) - Плановая загрузка оборудования в год (часы), т.е кол-во рабочих часов в год
    private int planHoursPerMonth;

    // Ф(г) - Фактическая загрузка оборудования в год (часы)
    private int factHoursPerMonth;

    // ПВЗ(%) - План выполненных заданий на текущую дату (процент)
    private int planForPlanDoneOnTheCurrentDatePerMonth;

    // Фактичне завантаження обладнання на місяць (відсоток)
    private int factPercentsPerMonth;


    // ФВЗ(%) - Факт выполненных заданий относительно их планового количества (проценты)
    // Т.е. сумма OperationTime (taskCondition.name = "Ок") к общей сумме OperationTime в месяц
    private int factForPlanDonePerMonth;

//    private int factHoursPerMonth;
//    // Процент загрузки оборудования в месяц
//    private int planFromTheTotal;
//    // Процент выполненных заданий в месяц (сумма OperationTime (taskCondition.name = "Ок") к общей сумме OperationTime в месяц)
//    private int doneFromPlan;
//    // Планируемый процент выполненных заданий в месяц на текущую дату (сумма OperationTime до текущей даты к общей сумме OperationTime в месяц )
//    private int planForTheCurrentDate;
    List<ChartDaysCommand> chartDaysCommands = new ArrayList<>();

    public ChartMonthCommand(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getPlanHoursPerMonth() {
        return planHoursPerMonth;
    }

    public void setPlanHoursPerMonth(int planHoursPerMonth) {
        this.planHoursPerMonth = planHoursPerMonth;
    }

    public int getFactHoursPerMonth() {
        return factHoursPerMonth;
    }

    public void setFactHoursPerMonth(int factHoursPerMonth) {
        this.factHoursPerMonth = factHoursPerMonth;
    }

    public int getFactPercentsPerMonth() {
        return factPercentsPerMonth;
    }

    public void setFactPercentsPerMonth(int factPercentsPerMonth) {
        this.factPercentsPerMonth = factPercentsPerMonth;
    }

    public int getPlanForPlanDoneOnTheCurrentDatePerMonth() {
        return planForPlanDoneOnTheCurrentDatePerMonth;
    }

    public void setPlanForPlanDoneOnTheCurrentDatePerMonth(int planForPlanDoneOnTheCurrentDatePerMonth) {
        this.planForPlanDoneOnTheCurrentDatePerMonth = planForPlanDoneOnTheCurrentDatePerMonth;
    }

    public int getFactForPlanDonePerMonth() {
        return factForPlanDonePerMonth;
    }

    public void setFactForPlanDonePerMonth(int factForPlanDonePerMonth) {
        this.factForPlanDonePerMonth = factForPlanDonePerMonth;
    }

    public List<ChartDaysCommand> getChartDaysCommands() {
        return chartDaysCommands;
    }

    public void setChartDaysCommands(List<ChartDaysCommand> chartDaysCommands) {
        this.chartDaysCommands = chartDaysCommands;
    }

    @Override
    public String toString() {
        return "ChartMonthCommand{" +
                "monthNumber=" + monthNumber +
                ", planHoursPerMonth=" + planHoursPerMonth +
                ", factHoursPerMonth=" + factHoursPerMonth +
                ", factPercentsPerMonth=" + factPercentsPerMonth +
                ", planForPlanDoneOnTheCurrentDatePerMonth=" + planForPlanDoneOnTheCurrentDatePerMonth +
                ", factForPlanDonePerMonth=" + factForPlanDonePerMonth +
                ", chartDaysCommands=" + chartDaysCommands +
                '}';
    }
}
