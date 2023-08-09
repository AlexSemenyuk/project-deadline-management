package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class EquipmentPlanService {
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;


    public List<ProductionPlan> formPlansOfCurrentYear(LocalDate date, Equipment equipment) {
        List<ProductionPlan> plansOfCurrentYear = new CopyOnWriteArrayList<>();
        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        int year = date.getYear();
//        int month = date.getMonthValue();
//        int daysOfMonth = TimeService.getDaysOfMonth(date);
//        System.out.println("year = " + year + ", month = " + month + ", daysOfMonth = " + daysOfMonth);
        productionPlanList.forEach(plan -> {
            if (plan.getCurrentStart().getYear() == year &&
                    Objects.equals(plan.getEquipment().getId(), equipment.getId())) {
                plansOfCurrentYear.add(plan);
            }
        });
        return plansOfCurrentYear;
    }

    public List<ProductionPlan> formPlansOfCurrentMonthForEquipment(LocalDate date, List<ProductionPlan> productionPlansPerYear) {
        List<ProductionPlan> plansOfCurrentMonth = new CopyOnWriteArrayList<>();
        int month = date.getMonthValue();
        if (!productionPlansPerYear.isEmpty()){
            productionPlansPerYear.forEach(plan -> {
                if (plan.getCurrentStart().getMonthValue() == month ) {
                    plansOfCurrentMonth.add(plan);
                }
            });
        }
       return plansOfCurrentMonth;
    }

    public List<ProductionPlan> formPlansOfCurrentDay(List<ProductionPlan> productionPlansPerCurrentMonth, int dayNumber) {
        List<ProductionPlan> plansOfCurrentDay = new CopyOnWriteArrayList<>();
        if (!productionPlansPerCurrentMonth.isEmpty()){
            productionPlansPerCurrentMonth.forEach(plan -> {
                if (plan.getCurrentStart().getDayOfMonth() == dayNumber) {
                    plansOfCurrentDay.add(plan);
                }
            });
        }
        return plansOfCurrentDay;
    }

    public ChartEquipmentCommand formChart(Equipment equipment, List<ProductionPlan> productionPlans, LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = TimeService.getDaysOfMonth(date);

        List<Equipment> equipmentList = equipmentRepository.findAll();
//        List<ChartEquipmentCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();

//        for (Equipment e : equipmentList) {
            ChartEquipmentCommand chartEquipmentCommand = new ChartEquipmentCommand(equipment.getId(), equipment.getNumber() + "-" + equipment.getName());
            for (int i = 0; i < daysOfMonth; i++) {
                LocalDate dayNumber = LocalDate.of(year, month, i + 1);
                ChartDaysCommand chartDaysCommand = new ChartDaysCommand(dayNumber);

                if (!productionPlans.isEmpty()) {
                    for (ProductionPlan plan : productionPlans) {
                        if (i == plan.getCurrentStart().getDayOfMonth() - 1) {
                            chartDaysCommand.getProductionPlans().add(plan);
                        }
                    }
                }
                chartDaysCommand.setDayColor(ChartDaysCommand.BLACK);
                chartEquipmentCommand.getChartDaysCommands().add(chartDaysCommand);
            }
//            chartEquipmentCommand.add(chartEquipmentCommand);
//        }
        chartEquipmentCommand = formPlanPerDayAndParameterPerMonth(chartEquipmentCommand);
        return chartEquipmentCommand;
    }

    private ChartEquipmentCommand formPlanPerDayAndParameterPerMonth(ChartEquipmentCommand chartEquipmentCommand) {
        // Количество часов в день
        int[] sum = new int[1];
        // Остаток от задания предыдущего дня
        int[] sumPrev = new int[1];
        // Время до полной смены в текущем дне
        int[] remainder = new int[1];
        // Дневная загрузка оборудования от плана (процент)
        int[] planPerDay = new int[1];
        // Количество часов в месяц
        int[] sumTotal = new int[1];
        // Количество выполненных часов в месяц ("ОК")
        int[] sumDone = new int[1];
        // Количество планируемых часов на текущую дату
        int[] sumOnCurrentDate = new int[1];

        String[] currentDayColorTmp = new String[1];

        LocalDate dateTmp = chartEquipmentCommand.getChartDaysCommands().get(0).getDayNumber();
        int planHoursPerMonth = TimeService.planHoursPerMonth(dateTmp);
//        chartEquipmentCommands.forEach(chartEquipmentCommand -> {
//            System.out.println("chartEquipmentCommand.getEquipment() = " + chartEquipmentCommand.getEquipment());
            sumTotal[0] = 0;
            sumDone[0] = 0;
            sumOnCurrentDate[0] = 0;

            chartEquipmentCommand.getChartDaysCommands().forEach(chartDaysCommand -> {
                currentDayColorTmp[0] = ChartDaysCommand.BLACK;
//                System.out.println("Обнова - currentDayColorTmp = " + currentDayColorTmp[0]);
                sum[0] = 0;
                if (!chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SUNDAY") &&
                        !chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SATURDAY")) {
                    if (sumPrev[0] > 0) {
                        if (sumPrev[0] > TimeService.HOURS_PER_DAY) {
                            sum[0] = TimeService.HOURS_PER_DAY;
                            sumPrev[0] -= TimeService.HOURS_PER_DAY;
                        } else {
                            sum[0] += sumPrev[0];
                            sumPrev[0] = 0;
                        }
                    }
                    if (chartDaysCommand.getProductionPlans().size() > 0) {
                        for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
                            int hour = plan.getCurrentStart().getHour();
//                        System.out.println("hour = " + hour);
                            if (hour + plan.getTask().getOperationTime() <= TimeService.HOURS_PER_DAY) {
                                sum[0] += plan.getTask().getOperationTime();
                            } else {
                                remainder[0] = TimeService.HOURS_PER_DAY - hour;
                                sumPrev[0] = plan.getTask().getOperationTime() - remainder[0];
                                sum[0] += remainder[0];
                                remainder[0] = 0;
                            }
//                        System.out.println("sum = " + sum[0] + "   - ProductionPlan");
                        }
                    }
//                System.out.println("sum = " + sum[0] + "   - Total");
                    planPerDay[0] = sum[0] * 100 / TimeService.HOURS_PER_DAY;
                    sumTotal[0] += sum[0];

                    if (chartDaysCommand.getProductionPlans().size() > 0) {
                        chartDaysCommand.getProductionPlans().forEach(p -> {
                            if (p.getTask().getTaskCondition().getName().equals("Ок")) {
                                sumDone[0] += p.getTask().getOperationTime();
                            }
                            if ((p.getCurrentStart()).isBefore(LocalDateTime.now())) {
                                sumOnCurrentDate[0] += p.getTask().getOperationTime();
                            }
                        });
                    }
//                    System.out.println("sum = " + sum[0] + "   - sumPrev");
                } else {
                    planPerDay[0] = 0;
                }
                // Определение цвета дня
                if (chartDaysCommand.getProductionPlans().size() > 0) {
                    chartDaysCommand.getProductionPlans().forEach(p -> {
                        if (chartDaysCommand.getDayNumber().isBefore(LocalDate.now())) {
                            if (currentDayColorTmp[0].equals("black") || currentDayColorTmp[0].equals("green")) {
                                if (p.getTask().getTaskCondition().getName().equals("Ок")) {
                                    currentDayColorTmp[0] = ChartDaysCommand.GREEN;
                                } else {
                                    currentDayColorTmp[0] = ChartDaysCommand.RED;
                                }
                            } else {
                                currentDayColorTmp[0] = ChartDaysCommand.RED;
                            }
                        }
                    });
                }
                chartDaysCommand.setDayColor(currentDayColorTmp[0]);
                chartDaysCommand.setPlanPerDay(planPerDay[0]);

            });
            if (sumTotal[0] != 0) {
                chartEquipmentCommand.setFactHoursPerMonth(sumTotal[0]);
                chartEquipmentCommand.setPlanFromTheTotal(sumTotal[0] * 100 / planHoursPerMonth);
                chartEquipmentCommand.setDoneFromPlan(sumDone[0] * 100 / sumTotal[0]);
                chartEquipmentCommand.setPlanForTheCurrentDate(sumOnCurrentDate[0] * 100 / sumTotal[0]);
            } else {
                chartEquipmentCommand.setPlanFromTheTotal(0);
                chartEquipmentCommand.setDoneFromPlan(0);
                chartEquipmentCommand.setPlanForTheCurrentDate(0);
            }
//        });

        return chartEquipmentCommand;
    }
}



