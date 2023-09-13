package org.itstep.projectdeadlinemanagement.api.service;

import lombok.*;
import org.itstep.projectdeadlinemanagement.api.data.ProjectChart;
import org.itstep.projectdeadlinemanagement.api.data.TermDate;
import org.itstep.projectdeadlinemanagement.model.Contract;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.ProductionPlanRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TaskService;
import org.itstep.projectdeadlinemanagement.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        LocalDate start = null;
        LocalDate deadline = null;
        TermDate projectTmp = new TermDate();
        TermDate designTmp = new TermDate();
        TermDate technologyTmp = new TermDate();
        TermDate materialContractsTmp = new TermDate();
        TermDate partProductionTmp = new TermDate();
        TermDate componentContractsTmp = new TermDate();
        TermDate assemblyProductionTmp = new TermDate();
        LocalDateTime deadlineTmp;

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent() && !productionPlans.isEmpty()) {
            Project currentProject = optionalProject.get();

            List<ProductionPlan> partProductionPlansForCurrentProject = new ArrayList<>();
            List<ProductionPlan> assemblyProductionPlansForCurrentProject = new ArrayList<>();
            for (ProductionPlan productionPlan : productionPlans) {
                if (Objects.equals(productionPlan.getTask().getProjectNumber(), currentProject.getNumber())) {
                    if (productionPlan.getTask().getTaskType().getId() == 1) {
                        partProductionPlansForCurrentProject.add(productionPlan);
                    }
                    if (productionPlan.getTask().getTaskType().getId() == 2) {
                        assemblyProductionPlansForCurrentProject.add(productionPlan);
                    }
                }
            }

            // ProjectTerm
            start = LocalDate.from(currentProject.getStart());
            deadline = LocalDate.from(currentProject.getDeadline());

            projectTmp.setStart(start);
            projectTmp.setDeadline(deadline);

            // DesignTerm
//            start[0] = LocalDate.from(project.getStart());
            start = TimeService.excludeWeekend(start);
            deadline = TimeService.localDateAddDays(start, currentProject.getDesignTerm() - 1);

            designTmp.setStart(start);
            designTmp.setDeadline(deadline);

            // TechnologyTerm
            start = TimeService.excludeWeekend(designTmp.getDeadline().plusDays(1));
            deadline = TimeService.localDateAddDays(start, currentProject.getTechnologyTerm() - 1);

            technologyTmp.setStart(start);
            technologyTmp.setDeadline(deadline);

            // materialContracts
            start = LocalDate.from(currentProject.getDeadline());
            deadline = LocalDate.from(currentProject.getStart());

            if (!currentProject.getContracts().isEmpty()) {
                for (Contract contract : currentProject.getContracts()) {
                    if (contract.getContractType().getId() == 1) {
                        LocalDate contractStartTmp = LocalDate.from(contract.getStart());
                        LocalDate contractDeadlineTmp = LocalDate.from(contract.getDeadline());
                        if (contractStartTmp.isBefore(start)) {
                            start = contractStartTmp;
                        }
                        if (contractDeadlineTmp.isAfter(deadline)) {
                            deadline = contractDeadlineTmp;
                        }
                    }
                }
            } else {
                start = LocalDate.from(currentProject.getStart());
                deadline = LocalDate.from(currentProject.getStart());
            }

            materialContractsTmp.setStart(start);
            materialContractsTmp.setDeadline(deadline);

            // partProductionPlan
            // Max from DesignOrTechnologyDeadline or Contracts
            if (technologyTmp.getDeadline().isAfter(materialContractsTmp.getDeadline())) {
                start = TimeService.excludeWeekend(technologyTmp.getDeadline().plusDays(1));
            } else {
                start = TimeService.excludeWeekend(materialContractsTmp.getDeadline().plusDays(1));
            }

            deadline = start;

            if (!partProductionPlansForCurrentProject.isEmpty()) {
                deadlineTmp = null;

                for (ProductionPlan plan : partProductionPlansForCurrentProject) {
                    deadlineTmp = TimeService.localDateTimeAddHours(plan.getCurrentStart(), plan.getTask().getOperationTime());
                    if (LocalDate.from(deadlineTmp).isAfter(deadline)) {
                        deadline = LocalDate.from(deadlineTmp);
                    }
                }
            }
            partProductionTmp.setStart(start);
            partProductionTmp.setDeadline(deadline);

            // componentContracts
            start = LocalDate.from(currentProject.getDeadline());
            deadline = LocalDate.from(currentProject.getStart());

            if (!currentProject.getContracts().isEmpty()) {
                for (Contract contract: currentProject.getContracts()){
                    if (contract.getContractType().getId() == 2) {
                        LocalDate contractStartTmp = LocalDate.from(contract.getStart());
                        LocalDate contractDeadlineTmp = LocalDate.from(contract.getDeadline());
                        if (contractStartTmp.isBefore(start)) {
                            start = contractStartTmp;
                        }
                        if (contractDeadlineTmp.isAfter(deadline)) {
                            deadline = contractDeadlineTmp;
                        }
                    }
                }

                if (start.equals(LocalDate.from(currentProject.getDeadline()))) {
                    start = LocalDate.from(currentProject.getStart());
                }
                if (deadline.equals(LocalDate.from(currentProject.getStart()))) {
                    deadline = LocalDate.from(currentProject.getStart());
                }
            } else {
                start = LocalDate.from(currentProject.getStart());
                deadline = LocalDate.from(currentProject.getStart());
            }
            componentContractsTmp.setStart(start);
            componentContractsTmp.setDeadline(deadline);

            // assemblyProductionPlan
            // Max from partProductionDeadline or componentContracts
            if (partProductionTmp.getDeadline().isAfter(componentContractsTmp.getDeadline())) {
                start = TimeService.excludeWeekend(partProductionTmp.getDeadline().plusDays(1));
            } else {
                start = TimeService.excludeWeekend(componentContractsTmp.getDeadline().plusDays(1));
//
            }
            deadline = start;

            if (!assemblyProductionPlansForCurrentProject.isEmpty()) {
                deadlineTmp = null;

                for (ProductionPlan plan : assemblyProductionPlansForCurrentProject) {
                    deadlineTmp = TimeService.localDateTimeAddHours(plan.getCurrentStart(), plan.getTask().getOperationTime());
                    if (LocalDate.from(deadlineTmp).isAfter(deadline)) {
                        deadline = LocalDate.from(deadlineTmp);
                    }
                }
            }
            assemblyProductionTmp.setStart(start);
            assemblyProductionTmp.setDeadline(deadline);
        }

        return new ProjectChart(projectTmp, designTmp, technologyTmp,
                materialContractsTmp, partProductionTmp, componentContractsTmp, assemblyProductionTmp);
    }


}
