package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartPlanCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/production_plans")
public class ProductionPlanController {
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;

    @GetMapping
    public String home(Model model) {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        model.addAttribute("equipmentList", equipmentList);

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        model.addAttribute("productionPlans", productionPlans);

        // Количество дней в месяце
        LocalDateTime date = LocalDateTime.now();
        int daysOfMonth = productionPlanService.getDaysOfMonth(date);
        int [] days = new int[daysOfMonth];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }
        model.addAttribute("days", days);

        List<ChartPlanCommand> chartPlanCommands = productionPlanService.formChartPlanCommand(productionPlans, days);
        model.addAttribute("chartPlanCommands", chartPlanCommands);

        return "production_plans";
    }
}
