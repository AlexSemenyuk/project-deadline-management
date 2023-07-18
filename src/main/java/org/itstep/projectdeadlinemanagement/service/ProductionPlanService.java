package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartPlanCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;

    public void formProductionPlans(List<Task> tasks) {
        int[] count = new int[1];
        count[0] = 0;

        tasks.forEach(task -> {
            // Номер в соответствии со списком ProductionPlans в Equipment
            count[0] = task.getEquipment().getProductionPlans().size();
            System.out.println("count = " + count[0]);

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
        int [] dayInMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int dayOfMonth = 0;
        boolean leapYear = false;
        if (year % 100 == 0){
            if (year % 400 == 0) {
                leapYear = true;
            }
        } else {
            if (year % 4 == 0){
                leapYear = true;
            }
        }
        if (leapYear){
            dayInMonths[1] = 29;
        }
        dayOfMonth = dayInMonths[month - 1];
        System.out.println("dayOfMonth = " + dayOfMonth);
        return dayOfMonth;
    }

    public List<ChartPlanCommand> formChartPlanCommand (List <ProductionPlan> productionPlans, int [] days){
        List<ChartPlanCommand> chartPlanCommands= null;
        LocalDateTime currentTimeTMP = LocalDateTime.now();

        return chartPlanCommands;
    }

}






