package org.itstep.projectdeadlinemanagement.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
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

    private final LocalDate DATE = LocalDate.now();

    @GetMapping
    public String home(Model model, RedirectAttributes modelR, HttpSession session) {

        System.out.println("GET-1 session.getAttribute = " + session.getAttribute("tmp"));
        System.out.println("GET-2 model.getAttribute = " + model.getAttribute("date"));

        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null){
            String currentDate = productionPlanService.formDate(DATE);
            System.out.println("currentDate = " + currentDate);
            session.setAttribute("tmp", currentDate);
            session.setAttribute("tmpDate", DATE);
        }
        model.addAttribute("currentDate", session.getAttribute("tmp"));
        System.out.println("GET-11 session.getAttribute = " + session.getAttribute("tmp"));
        System.out.println("GET-22 model.getAttribute = " + model.getAttribute("currentDate"));

        LocalDate dateForMonth = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", dateForMonth.getMonth());

        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(dateForMonth);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        // Данные в общую таблицу
        List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChart(productionPlansPerCurrentMonth, dateForMonth);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        // Данные за день
        model.addAttribute("productionPlansPerDay",
                chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getProductionPlans());

        String equipmentAndDayParameter = chartEquipmentCommands.get(0).getEquipment() + ";" +
                dateForMonth.getMonth() + " , " +
                chartEquipmentCommands.get(0).getChartDaysCommands().get(0).getDayNumber();
        model.addAttribute("parameter", equipmentAndDayParameter);
        return "production_plans";
    }

    @GetMapping("/{id}")
    String datails(@PathVariable String id, Model model, HttpSession session) {

        model.addAttribute("currentDate", session.getAttribute("tmp"));

        LocalDate dateForMonth = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", dateForMonth.getMonth());

        List<ProductionPlan> productionPlansPerCurrentMonth = productionPlanService.formPlansOfCurrentMonth(dateForMonth);
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);

        // Данные в общую таблицу
        List<ChartEquipmentCommand> chartEquipmentCommands = productionPlanService.formChart(productionPlansPerCurrentMonth, dateForMonth);
        model.addAttribute("chartEquipmentCommands", chartEquipmentCommands);
        // Данные за день
        String[] tmpId = id.split(":");
        int dayNumber = Integer.parseInt(tmpId[0]);
        int equipmentId = Integer.parseInt(tmpId[1]);

        List<ProductionPlan> productionPlansPerDay = chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getProductionPlans();
        model.addAttribute("productionPlansPerDay", productionPlansPerDay);

        String equipmentAndDayParameter = chartEquipmentCommands.get(equipmentId - 1).getEquipment() + ";" +
                dateForMonth.getMonth() + " , " +
                chartEquipmentCommands.get(equipmentId - 1).getChartDaysCommands().get(dayNumber - 1).getDayNumber();
        model.addAttribute("parameter", equipmentAndDayParameter);

        return "production_plans";
    }

    @PostMapping
    public String home(String month, RedirectAttributes model, HttpSession session) {
        formDateParameter(month, session);
        return "redirect:/production_plans";
    }

    @PostMapping("/{id}")
    String datails(@PathVariable String id, String month, Model model, HttpSession session) {
        formDateParameter(month, session);

//        String [] dateTmp = month.split("-");
//        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
//        System.out.println("month = " + month);
//        System.out.println("date = " + date);
//
//        String currentDate = productionPlanService.formDate(date);
//        System.out.println("POST currentDate = " + currentDate);
//        session.setAttribute("tmp", currentDate);
//        session.setAttribute("tmpDate", date);
//        System.out.println("POST session.getAttribute = " + session.getAttribute("tmp"));

        return "redirect:/production_plans/{id}";
    }
    @PostMapping("/date/{month}")
    String change(@PathVariable String month, HttpSession session) {
        System.out.println("monthFetch = " + month);
        formDateParameter(month, session);
//        String [] dateTmp = month.split("-");
//        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
//        System.out.println("month = " + month);
//        System.out.println("date = " + date);
//
//        String currentDate = productionPlanService.formDate(date);
//        System.out.println("POST currentDate = " + currentDate);
//        session.setAttribute("tmp", currentDate);
//        session.setAttribute("tmpDate", date);
//        System.out.println("POST session.getAttribute = " + session.getAttribute("tmp"));
        return "redirect:/production_plans";
    }


    private void formDateParameter(String month, HttpSession session) {
        String [] dateTmp = month.split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
        System.out.println("month = " + month);
        System.out.println("date = " + date);

        String currentDate = productionPlanService.formDate(date);
//        System.out.println("POST currentDate = " + currentDate);
        session.setAttribute("tmp", currentDate);
        session.setAttribute("tmpDate", date);
//        System.out.println("POST session.getAttribute = " + session.getAttribute("tmp"));
    }

}
