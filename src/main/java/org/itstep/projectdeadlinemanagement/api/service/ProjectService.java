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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanService productionPlanService;
    private final TaskService taskService;

    public ProjectChart formProjectChart(int id) {
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
//        dateTmp[0]
        int [] daysTmp = new int[1];
        int [] reminderTmp = new int[1];

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();

        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            projectTmp[0].setStart(LocalDate.from(project.getStart()));
            projectTmp[0].setDeadline(LocalDate.from(project.getDeadline()));

            designTmp[0].setStart(LocalDate.from(project.getStart()));
            designTmp[0].setDeadline(designTmp[0].getStart().plusDays(project.getDesignTerm()));

            technologyTmp[0].setStart(designTmp[0].getDeadline().plusDays(1));
            technologyTmp[0].setDeadline(technologyTmp[0].getStart().plusDays(project.getTechnologyTerm()));

            contractsTmp[0].setStart(LocalDate.from(project.getStart()));
            contractsTmp[0].setDeadline(LocalDate.from(project.getStart()));
            if (!project.getContracts().isEmpty()){
                project.getContracts().forEach(contract -> {
                    LocalDate contractStartTmp = LocalDate.from(contract.getStart());
                    LocalDate contractDeadlineTmp = LocalDate.from(contract.getDeadline());
                    if (contractDeadlineTmp.isAfter(contractsTmp[0].getDeadline())){
                        contractsTmp[0].setDeadline(contractDeadlineTmp);
                    }
                    if (contractsTmp[0].getStart().equals(LocalDate.from(project.getStart())) &&
                            contractStartTmp.isAfter(contractsTmp[0].getStart()) ){
                        contractsTmp[0].setStart(contractStartTmp);
                    } else if (contractStartTmp.isBefore(contractsTmp[0].getStart())){
                        contractsTmp[0].setStart(contractStartTmp);
                    }
                });
            }

            LocalDateTime productionStartTmp = taskService.formProductionStart(project);

            productionTmp[0].setStart(LocalDate.from(productionStartTmp));
            productionTmp[0].setDeadline(LocalDate.from(productionStartTmp));

            if (!productionPlans.isEmpty()){
                dateTmp[0] = null;
                daysTmp[0] = 0;
                productionPlans.forEach(plan -> {
                    if (plan.getTask().getProjectNumber() == project.getNumber()){
                        daysTmp[0] = plan.getTask().getOperationTime() / productionPlanService.HOURS_PER_DAY;
                        reminderTmp[0] = plan.getTask().getOperationTime() % productionPlanService.HOURS_PER_DAY;
                        if (reminderTmp[0] > 0){
                            daysTmp[0]++;
                        }
                        dateTmp[0] = LocalDate.from(plan.getCurrentStart().plusDays(daysTmp[0]));
                        if (dateTmp[0].isAfter(productionTmp[0].getDeadline())){
                            productionTmp[0].setDeadline(dateTmp[0]);
                        }

                    }
                });
            }


        });

        ProjectChart projectChart = new ProjectChart (projectTmp[0], designTmp[0], technologyTmp[0], contractsTmp[0], productionTmp[0]);
        System.out.println("projectChart.toString() = " + projectChart.toString());
        return projectChart;
    }
}
