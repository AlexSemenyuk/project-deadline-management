package org.itstep.projectdeadlinemanagement.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.command.ProductionPlanTermCommand;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.TaskCondition;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    private final TaskConditionRepository taskConditionRepository;

    private final LocalDate DATE = LocalDate.now();

    @GetMapping
    public String home(Model model, HttpSession session) {

        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null){
            String currentDate = TimeService.formDate(DATE);
//            System.out.println("currentDate = " + currentDate);
            session.setAttribute("tmp", currentDate);
            session.setAttribute("tmpDate", DATE);
        }

        formParameter(model, session, 1, 1);

        return "production_plans";
    }

    @GetMapping("/{id}")
    String datails(@PathVariable String id, Model model, HttpSession session) {

        String[] tmpId = id.split(":");
        int dayNumber = Integer.parseInt(tmpId[0]);
        int equipmentId = Integer.parseInt(tmpId[1]);

        formParameter(model, session, dayNumber, equipmentId);

        return "production_plans";
    }

    private void formParameter(Model model, HttpSession session,  int dayNumber, int equipmentId) {
        model.addAttribute("currentDate", session.getAttribute("tmp"));

        LocalDate dateForMonth = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", dateForMonth.getMonth());

        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(dateForMonth);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        // Данные в общую таблицу
        List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChart(productionPlansPerCurrentMonth, dateForMonth);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        // Данные за день
        List<ProductionPlan> productionPlansPerDay = chartEquipmentCommands.get(equipmentId - 1)
                .getChartDaysCommands().get(dayNumber - 1).getProductionPlans();

        model.addAttribute("productionPlansPerDay", productionPlansPerDay);

        String equipmentAndDayParameter = chartEquipmentCommands.get(equipmentId - 1)
                .getEquipment() + ";" + dateForMonth.getMonth() + " , " + chartEquipmentCommands.get(equipmentId - 1)
                .getChartDaysCommands().get(dayNumber - 1).getDayNumber();

        model.addAttribute("parameter", equipmentAndDayParameter);

        int planHoursPerMonth = productionPlanService.planHoursPerMonth(dateForMonth);
        model.addAttribute("planHoursPerMonth", planHoursPerMonth);

    }


    @PostMapping
    public String home(String month, RedirectAttributes model, HttpSession session) {
        formDateParameter(month, session);
        return "redirect:/production_plans";
    }

    @PostMapping("/{id}")
    String datails(@PathVariable String id, String month, Model model, HttpSession session) {
        formDateParameter(month, session);

        return "redirect:/production_plans/{id}";
    }

    @PostMapping("/date/{month}")
    String change(@PathVariable String month, HttpSession session) {
//        System.out.println("monthFetch = " + month);
        formDateParameter(month, session);
        return "redirect:/production_plans";
    }

    @GetMapping("/edit/{id}")
    String datails(@PathVariable Integer id, Model model) {
        Optional<ProductionPlan> productionPlan = productionPlanRepository.findById(id);
        productionPlan.ifPresent(plan -> {
            model.addAttribute("plan", plan);
        });
        List<TaskCondition> taskConditions = taskConditionRepository.findAll();
        model.addAttribute("taskConditions", taskConditions);
        return "production_plan_edit";
    }

    @PostMapping("/edit/{id}")
    String editPlan(@PathVariable Integer id, ProductionPlanTermCommand productionPlanTermCommand, Model model) {
        Optional<ProductionPlan> optionalProductionPlan = productionPlanRepository.findById(id);
        Optional<TaskCondition> optionalTaskCondition =
                taskConditionRepository.findById(productionPlanTermCommand.taskConditionId());
        if (optionalProductionPlan.isPresent() &&
        optionalTaskCondition.isPresent()){
            ProductionPlan productionPlan = optionalProductionPlan.get();

            LocalDateTime currentStart = productionPlanTermCommand.currentStart();
            productionPlan.setCurrentStart(currentStart);

            TaskCondition taskCondition = optionalTaskCondition.get();
            productionPlan.getTask().setTaskCondition(taskCondition);
            productionPlanRepository.save(productionPlan);
        }
        return "redirect:/production_plans/edit/{id}";
    }

    private void formDateParameter(String month, HttpSession session) {
        String [] dateTmp = month.split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
//        System.out.println("month = " + month);
//        System.out.println("date = " + date);
        String currentDate = TimeService.formDate(date);

        session.setAttribute("tmp", currentDate);
        session.setAttribute("tmpDate", date);
    }

}
