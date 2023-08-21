package org.itstep.projectdeadlinemanagement.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartMonthCommand;
import org.itstep.projectdeadlinemanagement.command.ChartYearCommand;
import org.itstep.projectdeadlinemanagement.command.ProductionPlanTermCommand;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.TaskCondition;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ChartService;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/production_plans")
public class ProductionPlanController {
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;
    private final ChartService chartService;
    private final TaskConditionRepository taskConditionRepository;


    @GetMapping
    public String home(Model model, HttpSession session) {

        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null) {
            String currentDate = TimeService.formDate(TimeService.DATE);
//            System.out.println("currentDate = " + currentDate);
            session.setAttribute("tmp", currentDate);

            session.setAttribute("tmpDate", TimeService.DATE);
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

    private void formParameter(Model model, HttpSession session, int dayNumber, int equipmentId) {
        model.addAttribute("currentDate", session.getAttribute("tmp"));

        LocalDate tmpDate = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", tmpDate.getMonth());

        int monthValue = tmpDate.getMonthValue() - 1;
        model.addAttribute("monthValue", monthValue);

        // 1. productionPlans за год
        List<ProductionPlan> productionPlansPerCurrentYear = productionPlanService.formPlansOfCurrentYear(tmpDate);

        // 2. Выборка productionPlans за месяц
        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(productionPlansPerCurrentYear, tmpDate);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        // 3. Выборка productionPlans за день
        List<ProductionPlan> productionPlansPerDay = productionPlanService.formPlansOfCurrentDate(productionPlansPerCurrentMonth, dayNumber, equipmentId);
//        List<ProductionPlan> productionPlansPerDay = chartMonthCommands.get(equipmentId - 1)
//                .getChartDaysCommands().get(dayNumber - 1).getProductionPlans();
        model.addAttribute("productionPlansPerDay", productionPlansPerDay);

        // 4. Данные для таблиц-графиков за год
        List<ChartYearCommand> chartYearCommands = chartService.formChart(productionPlansPerCurrentYear, tmpDate);
        model.addAttribute("chartYearCommands", chartYearCommands);

        // 5. Надпись для данных за день
        String equipmentAndDayParameter = chartYearCommands.get(equipmentId - 1).getEquipment() +
                "; " + tmpDate.getMonth() + " , " + dayNumber;
        model.addAttribute("parameter", equipmentAndDayParameter);

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
                optionalTaskCondition.isPresent()) {
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
        String[] dateTmp = month.split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
//        System.out.println("month = " + month);
//        System.out.println("date = " + date);
        String currentDate = TimeService.formDate(date);

        session.setAttribute("tmp", currentDate);
        session.setAttribute("tmpDate", date);
    }

}
