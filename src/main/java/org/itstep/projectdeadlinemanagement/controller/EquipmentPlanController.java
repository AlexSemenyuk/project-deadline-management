package org.itstep.projectdeadlinemanagement.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartEquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.itstep.projectdeadlinemanagement.service.EquipmentPlanService;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
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
    private final EquipmentPlanService equipmentPlanService;
    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null){
            String currentDate = TimeService.formDate(DATE);
//            System.out.println("currentDate = " + currentDate);
            session.setAttribute("tmp", currentDate);
            session.setAttribute("tmpDate", DATE);
        }

        formParameter(id, 1, model, session);
        return "equipment_plans";
    }

    private void formParameter(Integer equipmentId, int dayNumber, Model model, HttpSession session) {
        model.addAttribute("currentDate", session.getAttribute("tmp"));

        LocalDate dateForMonth = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", dateForMonth.getMonth());

        model.addAttribute("equipmentId", equipmentId);


        List<ProductionPlan> productionPlansPerYear = new CopyOnWriteArrayList<>();
        List<ProductionPlan> productionPlansPerCurrentMonth = new CopyOnWriteArrayList<>();
        List<ProductionPlan> productionPlansPerDay = new CopyOnWriteArrayList<>();
        ChartEquipmentCommand chartEquipmentCommand = null;

        Optional<Equipment> optionalEquipment = equipmentRepository.findById(equipmentId);
        if (optionalEquipment.isPresent()){
            Equipment equipment = optionalEquipment.get();
            productionPlansPerYear = equipmentPlanService.formPlansOfCurrentYear(dateForMonth, equipment);
            productionPlansPerCurrentMonth = equipmentPlanService.formPlansOfCurrentMonthForEquipment(dateForMonth,productionPlansPerYear);
            productionPlansPerDay = equipmentPlanService.formPlansOfCurrentDay(productionPlansPerCurrentMonth, dayNumber);
            chartEquipmentCommand = equipmentPlanService.formChart(equipment, productionPlansPerCurrentMonth, dateForMonth);
        }
        model.addAttribute("productionPlans", productionPlansPerCurrentMonth);
        model.addAttribute("productionPlansPerDay", productionPlansPerDay);
        model.addAttribute("chartEquipmentCommand", chartEquipmentCommand);

        String equipmentAndDayParameter = "";
        if (chartEquipmentCommand != null) {
            equipmentAndDayParameter = chartEquipmentCommand.getEquipment() + ";" +
                    dateForMonth.getMonth() + " , " +
                    chartEquipmentCommand.getChartDaysCommands().get(dayNumber - 1).getDayNumber();
        }
        model.addAttribute("parameter", equipmentAndDayParameter);

        int planHoursPerMonth = TimeService.planHoursPerMonth(dateForMonth);
        model.addAttribute("planHoursPerMonth", planHoursPerMonth);
    }

    @GetMapping("/{id}/day/{day}")
    String form(@PathVariable Integer id, @PathVariable String day, Model model, HttpSession session) {
//        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null){
//            String currentDate = TimeService.formDate(DATE);
////            System.out.println("currentDate = " + currentDate);
//            session.setAttribute("tmp", currentDate);
//            session.setAttribute("tmpDate", DATE);
//        }
        System.out.println("day = " + day);
        formParameter(id, Integer.parseInt(day), model, session);
        return "equipment_plans";
    }

    @PostMapping("/{id}/date/{month}")
    String change(@PathVariable Integer id, @PathVariable String month, HttpSession session) {
        System.out.println("monthFetch = " + month);
        formDateParameter(month, session);
        return "redirect:/production_plans/equipment_plans/{id}";
    }

    private void formDateParameter(String month, HttpSession session) {
        String [] dateTmp = month.split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateTmp[0]), Integer.parseInt(dateTmp[1]), 1);
        System.out.println("month = " + month);
        System.out.println("date = " + date);
        String currentDate = TimeService.formDate(date);

        session.setAttribute("tmp", currentDate);
        session.setAttribute("tmpDate", date);
    }
}


