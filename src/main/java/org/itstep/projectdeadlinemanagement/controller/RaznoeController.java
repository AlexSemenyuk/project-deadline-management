package org.itstep.projectdeadlinemanagement.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ChartMonthCommand;
import org.itstep.projectdeadlinemanagement.command.ChartYearCommand;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.itstep.projectdeadlinemanagement.service.ChartService;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/raznoe")
public class RaznoeController {
    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;
    private final ChartService chartService;


    @GetMapping
    public String home(Model model, HttpSession session) {
        List<Integer> years = new ArrayList<>();
        for (int i = 1900; i < 2051; i++) {
            years.add(i);
        }
        model.addAttribute("years", years);
        model.addAttribute("currentYear", session.getAttribute("curYear"));
//        session.setAttribute("currentYear", 2020);
        return "raznoe";
    }

    @PostMapping
    String changeDate(Integer year, Model model, HttpSession session, RedirectAttributes attributes) {
        System.out.println("year = " + year);
        session.setAttribute("curYear", year);
//        attributes.addAttribute("currentYear", year);
        return "redirect:/raznoe";
    }
}
