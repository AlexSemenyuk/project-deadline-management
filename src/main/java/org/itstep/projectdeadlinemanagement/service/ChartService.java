package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartMonthCommand;
import org.itstep.projectdeadlinemanagement.command.ChartYearCommand;
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
public class ChartService {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;

    public List<ChartYearCommand> formChart(List<ProductionPlan> productionPlans, LocalDate date) {
        int year = date.getYear();

        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<ChartYearCommand> chartYearCommands = new CopyOnWriteArrayList<>();

        for (Equipment e : equipmentList) {
            ChartYearCommand chartYearCommand = new ChartYearCommand(e.getId(), e.getNumber() + "-" + e.getName());
            for (int i = 0; i < TimeService.AMOUNT_OF_MONTHS_IN_A_YEAR; i++) {
                ChartMonthCommand chartMonthCommand = new ChartMonthCommand(i + 1);

                LocalDate dateTMP = LocalDate.of(year, chartMonthCommand.getMonthNumber(), 1);
                int daysOfMonth = TimeService.getDaysOfMonth(dateTMP);

                for (int j = 0; j < daysOfMonth; j++) {
                    LocalDate dayNumber = LocalDate.of(year, chartMonthCommand.getMonthNumber(), j + 1);
                    ChartDaysCommand chartDaysCommand = new ChartDaysCommand(dayNumber);

                    if (!productionPlans.isEmpty()) {
                        for (ProductionPlan plan : productionPlans) {
                            if (Objects.equals(e.getNumber(), plan.getEquipment().getNumber()) &&
                                    i == plan.getCurrentStart().getMonthValue() - 1 &&
                                    j == plan.getCurrentStart().getDayOfMonth() - 1) {
                                chartDaysCommand.getProductionPlans().add(plan);
                            }
                        }
                    }

                    chartDaysCommand.setDayColor(ChartDaysCommand.BLACK);
                    chartMonthCommand.getChartDaysCommands().add(chartDaysCommand);
                }
                chartYearCommand.getChartMonthCommands().add(chartMonthCommand);
            }

            chartYearCommands.add(chartYearCommand);
        }
        chartYearCommands = formPlanPerDayAndParameterPerYear(chartYearCommands, year);
        return chartYearCommands;
    }

    private List<ChartYearCommand> formPlanPerDayAndParameterPerYear(List<ChartYearCommand> chartYearCommands, int year) {
        // Количество часов в день

        int sum;
        // Остаток от задания предыдущего дня
        int sumPrev = 0;
        // Время до полной смены в текущем дне
        int remainder;
        // Дневная загрузка оборудования от плана (процент)
        int planPerDay;
        // Количество часов в месяц
        int sumTotal;
        // Количество выполненных часов в месяц ("ОК")
        int sumDone;
        // Количество планируемых часов на текущую дату
        int sumOnCurrentDate;

        String currentDayColorTmp = "";

        int planHoursPerYear;
        int factHoursPerYear;
        int sumOfPlanForPlanDoneOnTheCurrentDatePerYear;
        int sumOfFactForPlanDonePerYear;

        for (ChartYearCommand chartYearCommand : chartYearCommands) {
            planHoursPerYear = 0;
            factHoursPerYear = 0;
            sumOfPlanForPlanDoneOnTheCurrentDatePerYear = 0;
            sumOfFactForPlanDonePerYear = 0;

            for (ChartMonthCommand chartMonthCommand : chartYearCommand.getChartMonthCommands()) {
                sumTotal = 0;
                sumDone = 0;
                sumOnCurrentDate = 0;

                LocalDate dateTMP = LocalDate.of(year, chartMonthCommand.getMonthNumber(), 1);

                int planHoursPerMonth = TimeService.planHoursPerMonth(dateTMP);
                chartMonthCommand.setPlanHoursPerMonth(planHoursPerMonth);

                for (ChartDaysCommand chartDaysCommand : chartMonthCommand.getChartDaysCommands()) {
                    currentDayColorTmp = ChartDaysCommand.BLACK;
//                System.out.println("Обнова - currentDayColorTmp = " + currentDayColorTmp[0]);
                    sum = 0;
                    if (!chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SUNDAY") &&
                            !chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SATURDAY")) {
                        if (sumPrev > 0) {
                            if (sumPrev > TimeService.HOURS_PER_DAY) {
                                sum = TimeService.HOURS_PER_DAY;
                                sumPrev -= TimeService.HOURS_PER_DAY;
                            } else {
                                sum += sumPrev;
                                sumPrev = 0;
                            }
                        }
                        if (!chartDaysCommand.getProductionPlans().isEmpty()) {
                            for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
                                int hour = plan.getCurrentStart().getHour();
//                        System.out.println("hour = " + hour);
                                if (hour + plan.getTask().getOperationTime() <= TimeService.HOURS_PER_DAY) {
                                    sum += plan.getTask().getOperationTime();
                                } else {
                                    remainder = TimeService.HOURS_PER_DAY - hour;
                                    sumPrev = plan.getTask().getOperationTime() - remainder;
                                    sum += remainder;
                                    remainder = 0;
                                }
//                        System.out.println("sum = " + sum[0] + "   - ProductionPlan");
                            }
                        }
//                System.out.println("sum = " + sum[0] + "   - Total");
                        planPerDay = sum * 100 / TimeService.HOURS_PER_DAY;
                        sumTotal += sum;

                        if (!chartDaysCommand.getProductionPlans().isEmpty()) {
                            for (ProductionPlan p : chartDaysCommand.getProductionPlans()) {
                                if (p.getTask().getTaskCondition().getName().equals("Ок")) {
                                    sumDone += p.getTask().getOperationTime();
                                }
                                if ((p.getCurrentStart()).isBefore(LocalDateTime.now())) {
                                    sumOnCurrentDate += p.getTask().getOperationTime();
                                }
                            }
                        }
//                    System.out.println("sum = " + sum[0] + "   - sumPrev");
                    } else {
                        planPerDay = 0;
                    }
                    // Определение цвета дня
                    if (!chartDaysCommand.getProductionPlans().isEmpty()) {
                        for (ProductionPlan p : chartDaysCommand.getProductionPlans()) {
                            if (chartDaysCommand.getDayNumber().isBefore(LocalDate.now())) {
                                if (currentDayColorTmp.equals("black") || currentDayColorTmp.equals("green")) {
                                    if (p.getTask().getTaskCondition().getName().equals("Ок")) {
                                        currentDayColorTmp = ChartDaysCommand.GREEN;
                                    } else {
                                        currentDayColorTmp = ChartDaysCommand.RED;
                                    }
                                } else {
                                    currentDayColorTmp = ChartDaysCommand.RED;
                                }
                            }
                        }
                    }
                    chartDaysCommand.setDayColor(currentDayColorTmp);
                    chartDaysCommand.setPlanPerDay(planPerDay);
                }

                if (sumTotal != 0) {
                    chartMonthCommand.setFactHoursPerMonth(sumTotal);

//                    chartMonthCommand.setPlanFromTheTotal(sumTotal * 100 / planHoursPerMonth);
                    chartMonthCommand.setFactPercentsPerMonth(chartMonthCommand.getFactHoursPerMonth() * 100 / chartMonthCommand.getPlanHoursPerMonth());


//                    chartMonthCommand.setPlanForTheCurrentDate(sumOnCurrentDate * 100 / sumTotal);
                    chartMonthCommand.setPlanForPlanDoneOnTheCurrentDatePerMonth(sumOnCurrentDate * 100 / chartMonthCommand.getFactHoursPerMonth());

//                    chartMonthCommand.setDoneFromPlan(sumDone * 100 / sumTotal);
                    chartMonthCommand.setFactForPlanDonePerMonth(sumDone * 100 / chartMonthCommand.getFactHoursPerMonth());

                } else {
                    chartMonthCommand.setFactHoursPerMonth(0);
                    chartMonthCommand.setPlanForPlanDoneOnTheCurrentDatePerMonth(0);
                    chartMonthCommand.setFactForPlanDonePerMonth(0);
                }

                planHoursPerYear += chartMonthCommand.getPlanHoursPerMonth();
                factHoursPerYear += chartMonthCommand.getFactHoursPerMonth();
                sumOfPlanForPlanDoneOnTheCurrentDatePerYear += sumOnCurrentDate;
                sumOfFactForPlanDonePerYear += sumDone;

            }

            chartYearCommand.setPlanHoursPerYear(planHoursPerYear);
            chartYearCommand.setFactHoursPerYear(factHoursPerYear);
            chartYearCommand.setFactPercentsPerYear(chartYearCommand.getFactHoursPerYear() * 100 / chartYearCommand.getPlanHoursPerYear());
            chartYearCommand.setPlanForPlanDoneOnTheCurrentDatePerYear(sumOfPlanForPlanDoneOnTheCurrentDatePerYear * 100 / chartYearCommand.getPlanHoursPerYear());
            chartYearCommand.setFactForPlanDonePerYear(sumOfFactForPlanDonePerYear * 100 / chartYearCommand.getPlanHoursPerYear());


        }

        return chartYearCommands;
    }




//    public List<ChartMonthCommand> formChart(List<ProductionPlan> productionPlans, LocalDate date) {
//        int year = date.getYear();
//        int month = date.getMonthValue();
//        int daysOfMonth = TimeService.getDaysOfMonth(date);
//
//        List<Equipment> equipmentList = equipmentRepository.findAll();
//        List<ChartMonthCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();
//
//        for (Equipment e : equipmentList) {
//            ChartMonthCommand chartEquipmentCommand = new ChartMonthCommand(e.getId(), e.getNumber() + "-" + e.getName());
//            for (int i = 0; i < daysOfMonth; i++) {
//                LocalDate dayNumber = LocalDate.of(year, month, i + 1);
//                ChartDaysCommand chartDaysCommand = new ChartDaysCommand(dayNumber);
//
//                if (productionPlans.size() > 0) {
//                    for (ProductionPlan plan : productionPlans) {
//                        if (Objects.equals(e.getNumber(), plan.getEquipment().getNumber()) &&
//                                i == plan.getCurrentStart().getDayOfMonth() - 1) {
//                            chartDaysCommand.getProductionPlans().add(plan);
//                        }
//                    }
//                }
//
//                chartDaysCommand.setDayColor(ChartDaysCommand.BLACK);
//                chartEquipmentCommand.getChartDaysCommands().add(chartDaysCommand);
//            }
//            chartEquipmentCommands.add(chartEquipmentCommand);
//        }
//        chartEquipmentCommands = formPlanPerDayAndParameterPerMonth(chartEquipmentCommands);
//        return chartEquipmentCommands;
//    }
//
//    private List<ChartMonthCommand> formPlanPerDayAndParameterPerMonth(List<ChartMonthCommand> chartEquipmentCommands) {
//        // Количество часов в день
//        int[] sum = new int[1];
//        // Остаток от задания предыдущего дня
//        int[] sumPrev = new int[1];
//        // Время до полной смены в текущем дне
//        int[] remainder = new int[1];
//        // Дневная загрузка оборудования от плана (процент)
//        int[] planPerDay = new int[1];
//        // Количество часов в месяц
//        int[] sumTotal = new int[1];
//        // Количество выполненных часов в месяц ("ОК")
//        int[] sumDone = new int[1];
//        // Количество планируемых часов на текущую дату
//        int[] sumOnCurrentDate = new int[1];
//
//        String[] currentDayColorTmp = new String[1];
//
//        LocalDate dateTmp = chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
//        int planHoursPerMonth = TimeService.planHoursPerMonth(dateTmp);
//        chartEquipmentCommands.forEach(chartEquipmentCommand -> {
////            System.out.println("chartEquipmentCommand.getEquipment() = " + chartEquipmentCommand.getEquipment());
//            sumTotal[0] = 0;
//            sumDone[0] = 0;
//            sumOnCurrentDate[0] = 0;
//
//            chartEquipmentCommand.getChartDaysCommands().forEach(chartDaysCommand -> {
//                currentDayColorTmp[0] = ChartDaysCommand.BLACK;
////                System.out.println("Обнова - currentDayColorTmp = " + currentDayColorTmp[0]);
//                sum[0] = 0;
//                if (!chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SUNDAY") &&
//                        !chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SATURDAY")) {
//                    if (sumPrev[0] > 0) {
//                        if (sumPrev[0] > TimeService.HOURS_PER_DAY) {
//                            sum[0] = TimeService.HOURS_PER_DAY;
//                            sumPrev[0] -= TimeService.HOURS_PER_DAY;
//                        } else {
//                            sum[0] += sumPrev[0];
//                            sumPrev[0] = 0;
//                        }
//                    }
//                    if (chartDaysCommand.getProductionPlans().size() > 0) {
//                        for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
//                            int hour = plan.getCurrentStart().getHour();
////                        System.out.println("hour = " + hour);
//                            if (hour + plan.getTask().getOperationTime() <= TimeService.HOURS_PER_DAY) {
//                                sum[0] += plan.getTask().getOperationTime();
//                            } else {
//                                remainder[0] = TimeService.HOURS_PER_DAY - hour;
//                                sumPrev[0] = plan.getTask().getOperationTime() - remainder[0];
//                                sum[0] += remainder[0];
//                                remainder[0] = 0;
//                            }
////                        System.out.println("sum = " + sum[0] + "   - ProductionPlan");
//                        }
//                    }
////                System.out.println("sum = " + sum[0] + "   - Total");
//                    planPerDay[0] = sum[0] * 100 / TimeService.HOURS_PER_DAY;
//                    sumTotal[0] += sum[0];
//
//                    if (!chartDaysCommand.getProductionPlans().isEmpty()) {
//                        chartDaysCommand.getProductionPlans().forEach(p -> {
//                            if (p.getTask().getTaskCondition().getName().equals("Ок")) {
//                                sumDone[0] += p.getTask().getOperationTime();
//                            }
//                            if ((p.getCurrentStart()).isBefore(LocalDateTime.now())) {
//                                sumOnCurrentDate[0] += p.getTask().getOperationTime();
//                            }
//                        });
//                    }
////                    System.out.println("sum = " + sum[0] + "   - sumPrev");
//                } else {
//                    planPerDay[0] = 0;
//                }
//                // Определение цвета дня
//                if (!chartDaysCommand.getProductionPlans().isEmpty()) {
//                    chartDaysCommand.getProductionPlans().forEach(p -> {
//                        if (chartDaysCommand.getDayNumber().isBefore(LocalDate.now())) {
//                            if (currentDayColorTmp[0].equals("black") || currentDayColorTmp[0].equals("green")) {
//                                if (p.getTask().getTaskCondition().getName().equals("Ок")) {
//                                    currentDayColorTmp[0] = ChartDaysCommand.GREEN;
//                                } else {
//                                    currentDayColorTmp[0] = ChartDaysCommand.RED;
//                                }
//                            } else {
//                                currentDayColorTmp[0] = ChartDaysCommand.RED;
//                            }
//                        }
//                    });
//                }
//                chartDaysCommand.setDayColor(currentDayColorTmp[0]);
//                chartDaysCommand.setPlanPerDay(planPerDay[0]);
//
//            });
//            if (sumTotal[0] != 0) {
//                chartEquipmentCommand.setFactHoursPerMonth(sumTotal[0]);
//                chartEquipmentCommand.setPlanFromTheTotal(sumTotal[0] * 100 / planHoursPerMonth);
//                chartEquipmentCommand.setDoneFromPlan(sumDone[0] * 100 / sumTotal[0]);
//                chartEquipmentCommand.setPlanForTheCurrentDate(sumOnCurrentDate[0] * 100 / sumTotal[0]);
//            } else {
//                chartEquipmentCommand.setPlanFromTheTotal(0);
//                chartEquipmentCommand.setDoneFromPlan(0);
//                chartEquipmentCommand.setPlanForTheCurrentDate(0);
//            }
//        });
//        return chartEquipmentCommands;
//    }
}
