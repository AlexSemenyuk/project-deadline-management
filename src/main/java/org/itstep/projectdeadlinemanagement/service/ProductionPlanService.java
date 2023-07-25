package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.command.ChartPlanCommand;
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

                    // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)


//                    if (currentDeadlineTmp[0] != null) {        // Есть очередь - в конец
////                        if (currentDeadlineTmp[1].isAfter(currentDeadlineTmp[0])){
////                            currentDeadlineTmp[0] = currentDeadlineTmp[1];
////                        }
//                        if (productionPlan.getTask().getTermNumber() > 1) {
//                            currentDeadlineTmp[1] = getCurrentDeadlineFromPreviousTermNumber(productionPlan);
//                            if (currentDeadlineTmp[1].compareTo(currentDeadlineTmp[0]) > 0) {
//                                currentDeadlineTmp[0] = currentDeadlineTmp[1];
//                            }
//                        }
//                    } else {                                    // Если нет очереди - дата task
//                        currentDeadlineTmp[0] = task.getStartProduction();
//                    }
//                    productionPlan.setCurrentStart(currentDeadlineTmp[0]);
                    productionPlan.setCurrentStart(task.getStartProduction());
                    productionPlanRepository.save(productionPlan);
                    count[0]++;
//                    currentDeadlineTmp[0] = currentStartPlusOperationTime(productionPlan);
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
//                        if (currentDeadlineTmp[1].isAfter(currentDeadlineTmp[0])){
//                            currentDeadlineTmp[0] = currentDeadlineTmp[1];
//                        }
                            if (plan.getTask().getTermNumber() > 1) {
                                currentDeadlineTmp[1] = getCurrentDeadlineFromPreviousTermNumber(plan);
                                if (currentDeadlineTmp[1].compareTo(currentDeadlineTmp[0]) > 0) {
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
            System.out.println("termNumberCurrent = " + termNumberCurrent + " , termNumberPrevious = " + termNumberPrevious);
            if (Objects.equals(plan.getTask().getPartNumber(), productionPlan.getTask().getPartNumber()) &&
                    Objects.equals(plan.getTask().getLotNumber(), productionPlan.getTask().getLotNumber()) &&
                    termNumberCurrent == termNumberPrevious + 1) {
                previousCurrentDeadline[0] = currentStartPlusOperationTime(plan);
                System.out.println("previousCurrentDeadline = " + previousCurrentDeadline[0]);
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

    public List<ChartEquipmentCommand> formChartPlanCommand(List<Equipment> equipmentList, int daysOfMonth) {

        String[] equipment = new String[1];
        String[] part = new String[1];
        int[] day = new int[1];
        List<ChartEquipmentCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();

        for (Equipment e : equipmentList) {
            ChartEquipmentCommand chartEquipmentCommand = new ChartEquipmentCommand(e.getId(), e.getNumber() + ":" + e.getName());

            for (int i = 0; i < daysOfMonth; i++) {
                ChartDaysCommand chartDaysCommand = new ChartDaysCommand(i + 1);


                for (ProductionPlan plan : e.getProductionPlans()) {
                    day[0] = plan.getCurrentStart().getDayOfMonth();
//                    System.out.println("day = " + day[0]);
                    if (day[0] == i + 1) {
                        equipment[0] = e.getNumber() + "-" + e.getName();
                        part[0] = plan.getTask().getPartNumber() + "-" + plan.getTask().getPartName();

                        ChartPlanCommand chartPlanCommand = new ChartPlanCommand(
                                plan.getId(),
                                day[0],
                                plan.getNumber(),
                                plan.getCurrentStart(),
                                plan.getTask().getProjectNumber(),
                                equipment[0],
                                part[0],
                                plan.getTask().getTermNumber(),
                                plan.getTask().getOperationTime(),
                                plan.getTask().getLotNumber(),
                                plan.getTask().getStartProduction(),
                                plan.getTask().getTaskCondition().getName()
                        );
                        if (chartPlanCommand != null) {
                            chartDaysCommand.getChartPlanCommandList().add(chartPlanCommand);
                        }
                    }
                }
                int planPerDay = formPlanPerDay(chartDaysCommand);
                chartDaysCommand.setPlanPerDay(planPerDay);
                chartEquipmentCommand.getChartDaysCommands().add(chartDaysCommand);
            }
            chartEquipmentCommands.add(chartEquipmentCommand);
        }
        return chartEquipmentCommands;
    }

    private int formPlanPerDay(ChartDaysCommand chartDaysCommand) {
        int[] sum = new int[1];
        sum[0] = 0;
        if (chartDaysCommand.getChartPlanCommandList().size() > 0) {
            for (ChartPlanCommand plan : chartDaysCommand.getChartPlanCommandList()) {
                sum[0] += plan.getOperationTime();
            }
        }
        return sum[0] * 100 / HOURS_PER_DAY;
    }


}






