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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    public final int HOURS_PER_DAY = 8;

    public void formProductionPlans(List<Task> tasks) {
        int[] count = new int[1];
        count[0] = 0;
        tasks.forEach(task -> {
            // Номер в соответствии со списком ProductionPlans в Equipment
            count[0] = task.getEquipment().getProductionPlans().size();
//            System.out.println("count = " + count[0]);

            ProductionPlan productionPlan = new ProductionPlan(count[0] + 1);
            productionPlan.setTask(task);
            productionPlan.setCurrentStart(task.getStart());
            Optional<Equipment> optionalEquipment = equipmentRepository.findById(productionPlan.getTask().getEquipment().getId());
            optionalEquipment.ifPresent(productionPlan::setEquipment);
            productionPlanRepository.save(productionPlan);
        });
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
                                plan.getTask().getStart(),
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
        int [] sum = new int [1];
        sum[0] = 0;
        if (chartDaysCommand.getChartPlanCommandList().size() > 0){
            for (ChartPlanCommand plan: chartDaysCommand.getChartPlanCommandList()){
                sum[0] += plan.getOperationTime();
            }
        }
        return sum[0] * 100 / HOURS_PER_DAY;
    }


}






