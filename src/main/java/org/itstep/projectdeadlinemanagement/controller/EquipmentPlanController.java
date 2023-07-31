package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/production_plans/equipment_plans")
public class EquipmentPlanController {
    private final LocalDate DATE = LocalDate.now();
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;
    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Month month = DATE.getMonth();
        model.addAttribute("month", month);

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);

        List<ProductionPlan> productionPlansPerCurrentMonth = new CopyOnWriteArrayList<>();
        List<ChartEquipmentCommand> chartEquipmentCommands = new CopyOnWriteArrayList<>();

        List<ProductionPlan> productionPlansTMP = productionPlanService.formPlansOfCurrentMonth(DATE);
        List<ChartEquipmentCommand> chartEquipmentCommandsTMP = productionPlanService.formChart(productionPlansTMP, DATE);

        optionalEquipment.ifPresent(e -> {
            if (!productionPlansTMP.isEmpty()){
                for (ProductionPlan plan: productionPlansTMP){
                    if (Objects.equals(plan.getEquipment().getNumber(), e.getNumber())){
                        productionPlansPerCurrentMonth.add(plan);
                    }
                }
            }
            // Данные за месяц
            if (!chartEquipmentCommandsTMP.isEmpty()){
                for (ChartEquipmentCommand chartE: chartEquipmentCommandsTMP){
                    if (Objects.equals(chartE.getEquipmentId(), e.getId())){
                        chartEquipmentCommands.add(chartE);
                    }
                }
            }

            // Данные за день
            model.addAttribute("productionPlansPerDay",
                    chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getProductionPlans());

            String equipmentAndDayParameter = chartEquipmentCommands.get(0).getEquipment() + ";" +
                    month + " , " +
                    chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
            model.addAttribute("parameter", equipmentAndDayParameter);
        });
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        return "equipment_plans";
    }

}


