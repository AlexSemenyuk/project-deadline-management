package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;

    public void formProductionPlans(List<Task> tasks) {
        int[] count = new int[1];
        count[0] = 0;

        tasks.forEach(task -> {
            count[0] = (int) productionPlanRepository.count();
            System.out.println("count = " + count[0]);
            List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
            count[0] = productionPlanList.size();
            System.out.println("count = " + count[0]);
            ProductionPlan productionPlan = new ProductionPlan(count[0] + 1);
            productionPlan.setTask(task);
            productionPlan.setCurrentStart(task.getStart());
            productionPlanRepository.save(productionPlan);
        });
    }
}






