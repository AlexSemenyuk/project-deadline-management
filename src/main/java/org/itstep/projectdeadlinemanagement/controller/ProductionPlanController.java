package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/production_plans")
public class ProductionPlanController {
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;

    private final LocalDateTime DATE = LocalDateTime.now();

    @GetMapping
    public String home(Model model) {

        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(DATE);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        Month month = DATE.getMonth();
        model.addAttribute("month", month);

        // Данные в общую таблицу
        List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChart(productionPlansPerCurrentMonth, DATE);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        // Данные за день
        model.addAttribute("productionPlansPerDay",
                chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getProductionPlans());

        String equipmentAndDayParameter = chartEquipmentCommands.get(0).getEquipment() + ";" +
                month + " , " +
                chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
        model.addAttribute("parameter", equipmentAndDayParameter);
        return "production_plans";
    }

    @GetMapping("/{id}")
    String datails(@PathVariable String id, Model model) {

        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(DATE);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        Month month = DATE.getMonth();
        model.addAttribute("month", month);

        // Данные в общую таблицу
        List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChart(productionPlansPerCurrentMonth, DATE);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        // Данные за день
        String[] tmpId = id.split(":");
        int dayNumber = Integer.parseInt(tmpId[0]);
        int equipmentId = Integer.parseInt(tmpId[1]);

        List<ProductionPlan> productionPlansPerDay = chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getProductionPlans();
        model.addAttribute("productionPlansPerDay", productionPlansPerDay);

        String equipmentAndDayParameter = chartEquipmentCommands.get(equipmentId - 1).getEquipment() + ";" +
                month + " , " +
                chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getDayNumber();
        model.addAttribute("parameter", equipmentAndDayParameter);

        return "production_plans";
    }

}
