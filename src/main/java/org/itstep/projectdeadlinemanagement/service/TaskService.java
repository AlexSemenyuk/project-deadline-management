package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final EquipmentRepository equipmentRepository;

    public List<Task> formTasks(Integer id) {
        List<Task> tasks = new CopyOnWriteArrayList<>();
        Optional<Project> optionalProject = projectRepository.findById(id);
        int[] projectNumber = new int[1];
        projectNumber[0] = 0;

        int[] lotNumber = new int[1];
        lotNumber[0] = 0;

        LocalDateTime[] start = new LocalDateTime[1];
        start[0] = null;

        int[] amount = new int[1];
        amount[0] = 0;

        Task[] task = new Task[1];
        task[0] = null;

        Part[] part = new Part[1];
        part[0] = null;

        LocalDateTime [] productionStart = new LocalDateTime[1];

        optionalProject.ifPresent(project -> {
            projectNumber[0] = project.getNumber();
            productionStart[0] = formProductionStart(project);
            project.getProjectLists().forEach(projectList -> {
                part[0] = projectList.getPart();
                amount[0] = projectList.getAmount();
                for (int i = 0; i < amount[0]; i++) {
                    lotNumber[0] = i + 1;
//                    part[0].getTermParts().forEach(termPart -> {
                    start[0] = productionStart[0];
                    for (TechnologyPart termPart: part[0].getTermParts()) {
                        task[0] = Task.formTask(projectNumber[0],
                                part[0].getNumber(),
                                part[0].getName(),
                                termPart.getNumber(),
                                termPart.getOperationTime(),
                                lotNumber[0],
                                start[0]);
                        Optional<Equipment> optionalEquipment = equipmentRepository.findById(termPart.getEquipment().getId());
                        optionalEquipment.ifPresent(task[0]::setEquipment);
                        Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(1);
                        optionalTaskCondition.ifPresent(task[0]::setTaskCondition);
                        task[0].setProject(project);
                        tasks.add(task[0]);
                        start[0] = start[0].plusHours(termPart.getOperationTime());
                    }
//                    });
                }
            });
        });
        taskRepository.saveAll(tasks);
        return tasks;
    }

    private LocalDateTime formProductionStart(Project project) {
        LocalDateTime productionStart = project.getStart();
        // DesignTerm + TechnologyTerm
        LocalDateTime designAndTechnologyTMP = productionStart
                .plusDays(project.getDesignTerm())
                .plusDays(project.getTechnologyTerm());
        // Max from contract.getDeadline
        List<Contract> contracts = project.getContracts();
        LocalDateTime  contractTMP = project.getStart();
        for (Contract contract: contracts){
            if (contract.getDeadline().isAfter(contractTMP)) {
                contractTMP = contract.getDeadline();
            }
        }

        if (designAndTechnologyTMP.isAfter(contractTMP)){
            productionStart = designAndTechnologyTMP;
        } else {
            productionStart = contractTMP;
        }
        return productionStart;
    }


}




