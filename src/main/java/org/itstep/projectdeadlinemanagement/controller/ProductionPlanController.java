package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.command.ChartPlanCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
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
import java.util.concurrent.CopyOnWriteArrayList;

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
        List<Equipment> equipmentList = equipmentRepository.findAll();
        model.addAttribute("equipmentList", equipmentList);

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        model.addAttribute("productionPlans", productionPlans);

        // Количество дней в месяце и месяц
        int daysOfMonth = productionPlanService.getDaysOfMonth(DATE);
        Month month = DATE.getMonth();
        model.addAttribute("month", month);

//        if (!productionPlans.isEmpty()){
//            // Данные в общую таблицу
//            List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChartPlanCommand(equipmentList, daysOfMonth);
//            model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
//            // Данные за день
//            List<ChartPlanCommand> chartPlanCommandListPerDay = new CopyOnWriteArrayList<>();
//            model.addAttribute("chartPlanCommandListPerDay",
//                    chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getChartPlanCommandList());
//
//            String parameter = chartEquipmentCommands.get(0).getEquipment() + ";" +
//                    month + " , " +
//                    chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
//            model.addAttribute("parameter", parameter);
//        }
        return "production_plans";
    }

    @GetMapping("/{id}")
    String datails(@PathVariable String id, Model model) {

        List<Equipment> equipmentList = equipmentRepository.findAll();
        model.addAttribute("equipmentList", equipmentList);

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        model.addAttribute("productionPlans", productionPlans);

        // Количество дней в месяце и месяц
        int daysOfMonth = productionPlanService.getDaysOfMonth(DATE);
        Month month = DATE.getMonth();
        model.addAttribute("month", month);

        if (!productionPlans.isEmpty()){
            // Данные в общую таблицу
            List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChartPlanCommand(equipmentList, daysOfMonth);
            model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);

            // Данные за день
            String [] tmpId = id.split(":");
            int dayNumber = Integer.parseInt(tmpId[0]);
            int equipmentId = Integer.parseInt(tmpId[1]);

//            List<ChartPlanCommand> chartPlanCommandListPerDay = new CopyOnWriteArrayList<>();
            model.addAttribute("chartPlanCommandListPerDay",
                    chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getChartPlanCommandList());

            String parameter = chartEquipmentCommands.get(equipmentId - 1).getEquipment() + "; " +
                    month + " , " +
                    chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getDayNumber();
            model.addAttribute("parameter", parameter);
        }
        return "production_plans";
    }

}
