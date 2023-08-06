package org.itstep.projectdeadlinemanagement.api.service;

import lombok.*;
import org.itstep.projectdeadlinemanagement.api.data.ProjectChart;
import org.itstep.projectdeadlinemanagement.api.data.Term;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TaskService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectApiService {
    private final ProjectRepository projectRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanService productionPlanService;
    private final TaskService taskService;

    public ProjectChart formProjectChart(int id) {
        LocalDate [] start = new LocalDate[1];
        LocalDate [] deadline = new LocalDate[1];
        Term [] projectTmp = new Term[1];
        projectTmp[0] = new Term();
        Term [] designTmp = new Term[1];
        designTmp[0] = new Term();
        Term [] technologyTmp = new Term[1];
        technologyTmp[0] = new Term();
        Term [] contractsTmp = new Term[1];
        contractsTmp[0] = new Term();
        Term [] productionTmp = new Term[1];
        productionTmp[0] = new Term();
        LocalDate [] dateTmp = new LocalDate[1];
        int [] daysTmp = new int[1];
        int [] reminderTmp = new int[1];

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();

        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {

            // ProjectTerm
            start[0] = LocalDate.from(project.getStart());
            deadline[0] = LocalDate.from(project.getDeadline());

            projectTmp[0].setStart(start[0]);
            projectTmp[0].setDeadline(deadline[0]);

            // DesignTerm
//            start[0] = LocalDate.from(project.getStart());
            start[0] = TimeService.excludeWeekend(start[0]);
            deadline[0] = TimeService.localDateAddDays(start[0], project.getDesignTerm());

            designTmp[0].setStart(start[0]);
            designTmp[0].setDeadline(deadline[0]);
            System.out.println("designTmp[0].getStart() = " + designTmp[0].getStart());
            System.out.println("designTmp[0].getDeadline() = " + designTmp[0].getDeadline());

            // TechnologyTerm
            start[0] = TimeService.excludeWeekend(designTmp[0].getDeadline().plusDays(1));
            deadline[0] = TimeService.localDateAddDays(start[0], project.getTechnologyTerm());

            technologyTmp[0].setStart(start[0]);
            technologyTmp[0].setDeadline(deadline[0]);
            System.out.println("technologyTmp[0].getStart() = " + technologyTmp[0].getStart());
            System.out.println("technologyTmp[0].getDeadline() = " + technologyTmp[0].getDeadline());

            // Contracts
            start[0] = LocalDate.from(project.getDeadline());
            deadline[0] = LocalDate.from(project.getStart());

            if (!project.getContracts().isEmpty()){
                project.getContracts().forEach(contract -> {
                    LocalDate contractStartTmp = LocalDate.from(contract.getStart());
                    LocalDate contractDeadlineTmp = LocalDate.from(contract.getDeadline());
                    if (contractStartTmp.isBefore(start[0])){
                        start[0] = contractStartTmp;
                    }
                    if (contractDeadlineTmp.isAfter(deadline[0])){
                        deadline[0] = contractDeadlineTmp;
                    }
                });
            }

            contractsTmp[0].setStart(start[0]);
            contractsTmp[0].setDeadline(deadline[0]);
            System.out.println("contractsTmp[0].getStart() = " + contractsTmp[0].getStart());
            System.out.println("contractsTmp[0].getDeadline() = " + contractsTmp[0].getDeadline());

            // Max from DesignOrTechnologyDeadline or Contracts
            if (technologyTmp[0].getDeadline().isAfter(contractsTmp[0].getDeadline())){
                start[0] = technologyTmp[0].getDeadline().plusDays(1);
            } else {
                start[0] = contractsTmp[0].getDeadline().plusDays(1);
            }

            deadline[0] = start[0];

            if (!productionPlans.isEmpty()){
                dateTmp[0] = null;
                daysTmp[0] = 0;
                productionPlans.forEach(plan -> {
                    if (Objects.equals(plan.getTask().getProjectNumber(), project.getNumber())){
                        daysTmp[0] = plan.getTask().getOperationTime() / TimeService.HOURS_PER_DAY;
                        reminderTmp[0] = plan.getTask().getOperationTime() % TimeService.HOURS_PER_DAY;
                        if (reminderTmp[0] > 0){
                            daysTmp[0]++;
                        }

                        dateTmp[0] = TimeService.localDateAddDays(LocalDate.from(plan.getCurrentStart()), daysTmp[0]);
                        if (dateTmp[0].isAfter(deadline[0])){
                            deadline[0] = dateTmp[0];
                        }

                    }
                });
            }
            productionTmp[0].setStart(start[0]);
            productionTmp[0].setDeadline(deadline[0]);
            System.out.println("productionTmp[0].getStart() = " + productionTmp[0].getStart());
            System.out.println("productionTmp[0].getDeadline() = " + productionTmp[0].getDeadline());

        });

        ProjectChart projectChart = new ProjectChart (projectTmp[0], designTmp[0], technologyTmp[0], contractsTmp[0], productionTmp[0]);

        return projectChart;
    }



}
