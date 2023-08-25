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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/equipment_plans")
public class EquipmentPlanController {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProductionPlanService productionPlanService;
    private final ChartService chartService;

    @GetMapping
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null) {
            String currentDate = TimeService.formDate(TimeService.DATE);

            session.setAttribute("tmp", currentDate);
            session.setAttribute("tmpDate", TimeService.DATE);
        }

        formParameter(model, session, 1, 1);

        List<Integer> years = new ArrayList<>();
        for (int i = 1900; i < 2051; i++) {
            years.add(i);
        }
        model.addAttribute("years", years);


        return "equipment_plans";
    }

    @GetMapping("/{id}")
    public String home(@PathVariable String id, Model model, HttpSession session) {
        String[] idTmp = id.split(":");
        int equipmentId = Integer.parseInt(idTmp[0]);
        int monthNumber = Integer.parseInt(idTmp[1]);

        if (session.getAttribute("tmp") == null && session.getAttribute("tmpDate") == null) {
            String currentDate = TimeService.formDate(TimeService.DATE);

            session.setAttribute("tmp", currentDate);
            session.setAttribute("tmpDate", TimeService.DATE);
        }

        formParameter(model, session, equipmentId, monthNumber);
        return "equipment_plans";
    }

    private void formParameter(Model model, HttpSession session, int equipmentId, int monthNumber) {
        model.addAttribute("currentDate", session.getAttribute("tmp"));

        LocalDate tmpDate = (LocalDate) session.getAttribute("tmpDate");
        model.addAttribute("month", tmpDate.getMonth());
        model.addAttribute("year", tmpDate.getYear());

        int monthValue = tmpDate.getMonthValue() - 1;
        model.addAttribute("monthValue", monthValue);

        // 1. productionPlans за год
        List<ProductionPlan> productionPlansPerCurrentYear = productionPlanService.formPlansOfCurrentYear(tmpDate);

        // 2. Данные для таблиц-графиков за год
        List<ChartYearCommand> chartYearCommands = chartService.formChart(productionPlansPerCurrentYear, tmpDate);
        model.addAttribute("chartYearCommands", chartYearCommands);

        // 3. Показатели за месяц
        for (ChartYearCommand chartYearCommand : chartYearCommands) {
            if (chartYearCommand.getEquipmentId() == equipmentId) {
                model.addAttribute("equipmentValue", chartYearCommand.getEquipment());
                for (ChartMonthCommand chartMonthCommand : chartYearCommand.getChartMonthCommands()) {
                    if (chartMonthCommand.getMonthNumber() == monthNumber) {
                        model.addAttribute("planHoursPerMonth", chartMonthCommand.getPlanHoursPerMonth());
                        model.addAttribute("factHoursPerMonth", chartMonthCommand.getFactHoursPerMonth());
                        model.addAttribute("factPercentsPerMonth", chartMonthCommand.getFactPercentsPerMonth());
                        model.addAttribute("planForPlanDoneOnTheCurrentDatePerMonth", chartMonthCommand.getPlanForPlanDoneOnTheCurrentDatePerMonth());
                        model.addAttribute("factForPlanDonePerMonth", chartMonthCommand.getFactForPlanDonePerMonth());
                    }
                }
            }
        }
    }


    @PostMapping("/date/{year}")
    String changeDate(@PathVariable String year, HttpSession session) {
//        System.out.println("monthFetch = " + month);
//        formDateParameter(month, session);
        LocalDate tmpDate = (LocalDate) session.getAttribute("tmpDate");
        tmpDate = tmpDate.withYear(Integer.parseInt(year));
        session.setAttribute("tmpDate", tmpDate);
        return "redirect:/equipment_plans";
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


