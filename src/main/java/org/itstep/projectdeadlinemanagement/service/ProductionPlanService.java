package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    public final int HOURS_PER_DAY = 8;


    public void formProductionPlans(List<Task> tasks) {

        List<Equipment> equipmentList = equipmentRepository.findAll();

        // Номер по списку ProductionPlans для данного оборудования
        int[] count = new int[1];

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();

        // формирование ProductionPlans
        equipmentList.forEach(equipment -> {
            count[0] = 0;

            // - Определение существующего списка ProductionPlans для данного оборудования
            // - Получение данных с последнего экземпляра
            productionPlans.forEach(productionPlan -> {
                if (Objects.equals(productionPlan.getEquipment().getNumber(), equipment.getNumber())) {
                    count[0]++;
                }
            });

            tasks.forEach(task -> {
                if (Objects.equals(equipment.getNumber(), task.getEquipment().getNumber())) {
                    ProductionPlan productionPlan = new ProductionPlan(count[0] + 1);
                    productionPlan.setTask(task);

                    Optional<Equipment> optionalEquipment = equipmentRepository.findById(productionPlan.getTask().getEquipment().getId());
                    optionalEquipment.ifPresent(productionPlan::setEquipment);

                    productionPlan.setCurrentStart(task.getStartProduction());
                    productionPlanRepository.save(productionPlan);
                    count[0]++;
                }
            });
        });
        formCurrentStart();
    }

    private void formCurrentStart() {
        // Дата + время, описывающее окончание предыдущего по списку ProductionPlans для данного оборудования
        LocalDateTime[] currentDeadlineTmp = new LocalDateTime[2];
        List<Equipment> equipmentList = equipmentRepository.findAll();

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ProductionPlan::getNumber))
                .collect(Collectors.toList());

        equipmentList.forEach(equipment -> {
//            if (equipment != null) {
//                System.out.println(equipment.getNumber() + "-" + equipment.getName());
//            }

            // DateTime является CurrentStart для текущего (является Deadline, с учетом OperationTime от предыдущего ProductionPlan с состоянием не "New")
            currentDeadlineTmp[0] = null;
            // DateTime является CurrentStart для текущего с учетом termNumber
            currentDeadlineTmp[1] = null;

            productionPlans.forEach(plan -> {
                if (Objects.equals(plan.getEquipment().getNumber(), equipment.getNumber())) {

                    if (!plan.getTask().getTaskCondition().getName().equals("New")) {
                        // Определение start для текущего (окончание предыдущего ProductionPlan)
                        currentDeadlineTmp[0] = currentStartPlusOperationTime(plan);
                    } else {                                                // Работаем только с состоянием "New"
                        // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
                        if (currentDeadlineTmp[0] != null) {                // Есть очередь (есть предыдущий) - в конец
                            // Коррекция по termNumber
                            if (plan.getTask().getTermNumber() > 1) {
                                currentDeadlineTmp[1] = getCurrentDeadlineFromPreviousTermNumber(plan);
                                if (currentDeadlineTmp[1].isAfter(currentDeadlineTmp[0])) {
                                    currentDeadlineTmp[0] = currentDeadlineTmp[1];
                                }
                            }
                        } else {                                    // Если нет очереди - дата task
                            currentDeadlineTmp[0] = plan.getTask().getStartProduction();
                            currentDeadlineTmp[0] = excludeWeekend(currentDeadlineTmp[0]);
                        }
                        plan.setCurrentStart(currentDeadlineTmp[0]);
                        productionPlanRepository.save(plan);
                        currentDeadlineTmp[0] = currentStartPlusOperationTime(plan);
                    }
                }
            });
        });
    }

    private LocalDateTime currentStartPlusOperationTime(ProductionPlan productionPlan) {
        LocalDateTime rezult = productionPlan.getCurrentStart();
//        System.out.println("rezult1 = " + rezult);

        rezult = excludeWeekend(rezult);
//        System.out.println("rezult2 = " + rezult);

        int timeTMP = productionPlan.getCurrentStart().getHour() + productionPlan.getTask().getOperationTime();
        int days = timeTMP / HOURS_PER_DAY;
        int hours = timeTMP % HOURS_PER_DAY;
        rezult = rezult.plusDays(days).withHour(hours);

//        System.out.println("rezult3 = " + rezult);

        rezult = excludeWeekend(rezult);

//        System.out.println("rezult4 = " + rezult);
        return rezult;
    }

    private static LocalDateTime excludeWeekend(LocalDateTime date) {
        switch (date.getDayOfWeek().toString()) {
            case "SUNDAY" -> {
                return date.plusDays(1);
            }
            case "SATURDAY" -> {
                return date.plusDays(2);
            }
        }
        return date;
    }

    // Сохранение цепочки termNumber - Получение currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
    private LocalDateTime getCurrentDeadlineFromPreviousTermNumber(ProductionPlan productionPlan) {
        LocalDateTime[] previousCurrentDeadline = new LocalDateTime[1];
        previousCurrentDeadline[0] = null;
        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        productionPlanList.forEach(plan -> {
            int termNumberCurrent = productionPlan.getTask().getTermNumber();
            int termNumberPrevious = plan.getTask().getTermNumber();
            if (Objects.equals(plan.getTask().getPartNumber(), productionPlan.getTask().getPartNumber()) &&
                    Objects.equals(plan.getTask().getLotNumber(), productionPlan.getTask().getLotNumber()) &&
                    termNumberCurrent == termNumberPrevious + 1) {
                previousCurrentDeadline[0] = currentStartPlusOperationTime(plan);
            }
        });
        return previousCurrentDeadline[0];
    }


    public List<ProductionPlan> formPlansOfCurrentMonth(LocalDate date) {

        List<ProductionPlan> plansOfCurrentMonth = new CopyOnWriteArrayList<>();
        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = getDaysOfMonth(date);
//        System.out.println("year = " + year + ", month = " + month + ", daysOfMonth = " + daysOfMonth);
        productionPlanList.forEach(plan -> {
            if (plan.getCurrentStart().getYear() == year &&
                    plan.getCurrentStart().getMonthValue() == month) {
                plansOfCurrentMonth.add(plan);
            }
        });

        return plansOfCurrentMonth;
    }

    public List<ChartEquipmentCommand> formChart(List<ProductionPlan> productionPlans, LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = getDaysOfMonth(date);

        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<ChartEquipmentCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();

        for (Equipment e : equipmentList) {
            ChartEquipmentCommand chartEquipmentCommand = new ChartEquipmentCommand(e.getId(), e.getNumber() + "-" + e.getName());
            for (int i = 0; i < daysOfMonth; i++) {
                LocalDate dayNumber = LocalDate.of(year, month, i + 1);
                ChartDaysCommand chartDaysCommand = new ChartDaysCommand(dayNumber);

                if (productionPlans.size() > 0) {
                    for (ProductionPlan plan : productionPlans) {
                        if (Objects.equals(e.getNumber(), plan.getEquipment().getNumber()) &&
                                i == plan.getCurrentStart().getDayOfMonth() - 1) {
                            chartDaysCommand.getProductionPlans().add(plan);
                        }
                    }
                }

                chartDaysCommand.setDayColor(ChartDaysCommand.BLACK);
                chartEquipmentCommand.getChartDaysCommands().add(chartDaysCommand);
            }
            chartEquipmentCommands.add(chartEquipmentCommand);
        }
        chartEquipmentCommands = formPlanPerDayAndParameterPerMonth(chartEquipmentCommands);
        return chartEquipmentCommands;
    }

    public int planHoursPerMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = getDaysOfMonth(date);
        int sum = 0;
        for (int i = 0; i < daysOfMonth; i++) {
            LocalDate dateTmp = LocalDate.of(year, month, i + 1);
            if (!dateTmp.getDayOfWeek().toString().equals("SUNDAY") &&
                    !dateTmp.getDayOfWeek().toString().equals("SATURDAY")) {
                sum += HOURS_PER_DAY;
            }
        }
        return sum;
    }

    private List<ChartEquipmentCommand> formPlanPerDayAndParameterPerMonth(List<ChartEquipmentCommand> chartEquipmentCommands) {
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

        LocalDate dateTmp = chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
        int planHoursPerMonth = planHoursPerMonth(dateTmp);
        chartEquipmentCommands.forEach(chartEquipmentCommand -> {
//            System.out.println("chartEquipmentCommand.getEquipment() = " + chartEquipmentCommand.getEquipment());
            sumTotal[0] = 0;
            sumDone[0] = 0;
            sumOnCurrentDate[0] = 0;

            chartEquipmentCommand.getChartDaysCommands().forEach(chartDaysCommand -> {
                currentDayColorTmp[0] = ChartDaysCommand.BLACK;
                System.out.println("Обнова - currentDayColorTmp = " + currentDayColorTmp[0]);

//                System.out.println("chartDaysCommand.getDayNumber() = " + chartDaysCommand.getDayNumber());
                sum[0] = 0;
                if (!chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SUNDAY") &&
                        !chartDaysCommand.getDayNumber().getDayOfWeek().toString().equals("SATURDAY")) {
                    if (sumPrev[0] > 0) {
                        if (sumPrev[0] > HOURS_PER_DAY) {
                            sum[0] = HOURS_PER_DAY;
                            sumPrev[0] -= HOURS_PER_DAY;
                        } else {
                            sum[0] += sumPrev[0];
                            sumPrev[0] = 0;
                        }
                    }
                    if (chartDaysCommand.getProductionPlans().size() > 0) {
                        for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
                            int hour = plan.getCurrentStart().getHour();
//                        System.out.println("hour = " + hour);
                            if (hour + plan.getTask().getOperationTime() <= HOURS_PER_DAY) {
                                sum[0] += plan.getTask().getOperationTime();
                            } else {
                                remainder[0] = HOURS_PER_DAY - hour;
                                sumPrev[0] = plan.getTask().getOperationTime() - remainder[0];
                                sum[0] += remainder[0];
                                remainder[0] = 0;
                            }
//                        System.out.println("sum = " + sum[0] + "   - ProductionPlan");
                        }
                    }
//                System.out.println("sum = " + sum[0] + "   - Total");
                    planPerDay[0] = sum[0] * 100 / HOURS_PER_DAY;
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
//
//                            if (p.getTask().getTaskCondition().getName().equals("Ок") &&
//                                    (currentDayColorTmp[0].equals("black") || currentDayColorTmp[0].equals("green"))){
//                                currentDayColorTmp[0] = ChartDaysCommand.GREEN;
//                            } else {
//                                currentDayColorTmp[0] = ChartDaysCommand.RED;
//                            }
                            System.out.println("currentDayColorTmp = " + currentDayColorTmp[0]);
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
        });

        return chartEquipmentCommands;
    }

    public String formDate(LocalDate dateTmp) {
        int month = dateTmp.getMonthValue();
        int year = dateTmp.getYear();
        String date = "";
        if (month < 10) {
            date = year + "-0" + month;
        } else {
            date = year + "-" + month;
        }
        return date;
    }

    public int getDaysOfMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int[] dayInMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int dayOfMonth = 0;
        boolean leapYear = false;
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                leapYear = true;
            }
        } else {
            if (year % 4 == 0) {
                leapYear = true;
            }
        }
        if (leapYear) {
            dayInMonths[1] = 29;
        }
        dayOfMonth = dayInMonths[month - 1];
        return dayOfMonth;
    }
}






