package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

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
//                    currentDeadlineTmp[0] = currentStartPlusOperationTime(productionPlan);
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
        // Дата по списку ProductionPlans для данного оборудования
        LocalDateTime[] currentDeadlineTmp = new LocalDateTime[2];

//        int[] count = new int[1];
        List<Equipment> equipmentList = equipmentRepository.findAll();
//        List<ProductionPlan>[] productionPlans = new List<ProductionPlan>[1];
        List<ProductionPlan> productionPlans = productionPlanRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ProductionPlan::getNumber))
                .collect(Collectors.toList());
        equipmentList.forEach(equipment -> {
            currentDeadlineTmp[0] = null;
            currentDeadlineTmp[1] = null;
            productionPlans.forEach(plan -> {
                if (Objects.equals(plan.getEquipment().getNumber(), equipment.getNumber())) {
                    if (!plan.getTask().getTaskCondition().getName().equals("New")) {
                        currentDeadlineTmp[0] = currentStartPlusOperationTime(plan);
                    } else {
                        // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
                        if (currentDeadlineTmp[0] != null) {        // Есть очередь - в конец
                            if (plan.getTask().getTermNumber() > 1) {
                                currentDeadlineTmp[1] = getCurrentDeadlineFromPreviousTermNumber(plan);
                                if (currentDeadlineTmp[1].isAfter(currentDeadlineTmp[0])) {
                                    currentDeadlineTmp[0] = currentDeadlineTmp[1];
                                }
                            }
                        } else {                                    // Если нет очереди - дата task
                            currentDeadlineTmp[0] = plan.getTask().getStartProduction();
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
        int timeTMP = productionPlan.getCurrentStart().getHour() + productionPlan.getTask().getOperationTime();
        int days = timeTMP / HOURS_PER_DAY;
        int hours = timeTMP % HOURS_PER_DAY;
        return productionPlan.getCurrentStart().plusDays(days).withHour(hours);
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


    public int getDaysOfMonth(LocalDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
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

    public List<ProductionPlan> formPlansOfCurrentMonth(LocalDateTime date) {
        List<ProductionPlan> plansOfCurrentMonth = new CopyOnWriteArrayList<>();
        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = getDaysOfMonth(date);
        System.out.println("year = " + year + ", month = " + month + ", daysOfMonth = " + daysOfMonth);
        productionPlanList.forEach(plan -> {
            if (plan.getCurrentStart().getYear() == year &&
                    plan.getCurrentStart().getMonthValue() == month) {
                plansOfCurrentMonth.add(plan);
            }
        });

        return plansOfCurrentMonth;
    }

    public List<ChartEquipmentCommand> formChart(List<ProductionPlan> productionPlans, LocalDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = getDaysOfMonth(date);
        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<ChartEquipmentCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();

        for (Equipment e : equipmentList) {
            ChartEquipmentCommand chartEquipmentCommand = new ChartEquipmentCommand(e.getId(), e.getNumber() + "-" + e.getName());
            for (int i = 0; i < daysOfMonth; i++) {
                ChartDaysCommand chartDaysCommand = new ChartDaysCommand(i + 1);

                if (productionPlans.size() > 0) {
                    for (ProductionPlan plan : productionPlans) {
                        if (Objects.equals(e.getNumber(), plan.getEquipment().getNumber()) &&
                                i == plan.getCurrentStart().getDayOfMonth() - 1) {
                            chartDaysCommand.getProductionPlans().add(plan);
                        }
                    }
                }
//                int planPerDay = formPlanPerDay(chartDaysCommand);
//                chartDaysCommand.setPlanPerDay(planPerDay);
                chartEquipmentCommand.getChartDaysCommands().add(chartDaysCommand);
            }
            chartEquipmentCommands.add(chartEquipmentCommand);
        }
        chartEquipmentCommands = formPlanPerDay(chartEquipmentCommands);
        return chartEquipmentCommands;
    }

//    private int formPlanPerDay(ChartDaysCommand chartDaysCommand) {
//        int[] sum = new int[1];
//        sum[0] = 0;
//        if (chartDaysCommand.getProductionPlans().size() > 0) {
//            for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
//                sum[0] += plan.getTask().getOperationTime();
//            }
//        }
//        return sum[0] * 100 / HOURS_PER_DAY;
//    }
    private List<ChartEquipmentCommand> formPlanPerDay(List<ChartEquipmentCommand> chartEquipmentCommands) {
        int[] sum = new int[1];
        int[] sumPrev = new int[1];
        int[] remainder = new int[1];
        int [] planPerDay = new int[1];
        chartEquipmentCommands.forEach(chartEquipmentCommand -> {
            System.out.println("chartEquipmentCommand.getEquipment() = " + chartEquipmentCommand.getEquipment());
            chartEquipmentCommand.getChartDaysCommands().forEach(chartDaysCommand -> {
                System.out.println("chartDaysCommand.getDayNumber() = " + chartDaysCommand.getDayNumber());
                sum[0] = 0;
                if (sumPrev[0] > 0){
                    if (sumPrev[0] > HOURS_PER_DAY){
                        sum[0] = HOURS_PER_DAY;
                        sumPrev[0] -= HOURS_PER_DAY;
                    } else {
                        sum[0] += sumPrev[0];
                        sumPrev[0] = 0;
                    }
                    System.out.println("sum = " + sum[0] + "   - sumPrev");
                }
                if (chartDaysCommand.getProductionPlans().size() > 0) {
                    for (ProductionPlan plan : chartDaysCommand.getProductionPlans()) {
                        int hour = plan.getCurrentStart().getHour();
                        System.out.println("hour = " + hour);
                        if (hour + plan.getTask().getOperationTime() <= HOURS_PER_DAY){
                            sum[0] += plan.getTask().getOperationTime();
                        } else {
                            remainder[0] = HOURS_PER_DAY - hour;
                            sumPrev[0] = plan.getTask().getOperationTime() - remainder[0];
                            sum[0] += remainder[0];
                            remainder[0] = 0;
                        }
                        System.out.println("sum = " + sum[0] + "   - ProductionPlan");
                    }
                }
                System.out.println("sum = " + sum[0] + "   - Total");
                planPerDay[0] = sum[0] * 100 / HOURS_PER_DAY;
                chartDaysCommand.setPlanPerDay(planPerDay[0]);
            });
        });

        return chartEquipmentCommands;
    }

}






