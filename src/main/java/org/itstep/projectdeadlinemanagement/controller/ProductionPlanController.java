package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.command.ChartPlanCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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

        // Количество дней в месяце
        int daysOfMonth = productionPlanService.getDaysOfMonth(DATE);

        int [] days = new int[daysOfMonth];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }
        model.addAttribute("days", days);

        if (!productionPlans.isEmpty()){
            List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChartPlanCommand(equipmentList, daysOfMonth);
            model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
            List<ChartPlanCommand> chartPlanCommandListPerDay = new CopyOnWriteArrayList<>();
            model.addAttribute("chartPlanCommandListPerDay",
                    chartEquipmentCommands.get(1).getChartDaysCommands().get(23).getChartPlanCommandList());
        }
        return "production_plans";
    }

    @GetMapping("/{id}")
    String datails(@PathVariable String id, Model model) {

        List<Equipment> equipmentList = equipmentRepository.findAll();
        model.addAttribute("equipmentList", equipmentList);

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        model.addAttribute("productionPlans", productionPlans);

        // Количество дней в месяце
        int daysOfMonth = productionPlanService.getDaysOfMonth(DATE);

        int [] days = new int[daysOfMonth];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }
        model.addAttribute("days", days);

        if (!productionPlans.isEmpty()){
            List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChartPlanCommand(equipmentList, daysOfMonth);
            model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);

            String [] tmpId = id.split(":");
            int dayNumber = Integer.parseInt(tmpId[0]);
            int equipmentId = Integer.parseInt(tmpId[1]);
            System.out.println("tmpId = " + Arrays.toString(tmpId));
            List<ChartPlanCommand> chartPlanCommandListPerDay = new CopyOnWriteArrayList<>();

//            label:
            for (ChartEquipmentCommand e: chartEquipmentCommands) {
                if (equipmentId == e.getEquipmentId()){
                    for (ChartDaysCommand d: e.getChartDaysCommands()){
                        if (dayNumber == d.getDayNumber()){
                            d.getChartPlanCommandList().forEach(plan -> {
                                chartPlanCommandListPerDay.add(plan);
                            });
                            chartPlanCommandListPerDay.forEach(System.out::println);
                            model.addAttribute("chartPlanCommandListPerDay", chartPlanCommandListPerDay);
//                            break label;
                        }
                    }
                }
            }
        }
        return "production_plans";
//        return "redirect:/production_plans";
    }

}
