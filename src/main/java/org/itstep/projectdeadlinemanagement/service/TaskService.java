package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

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

    public void formTasks(Integer id) {
        List<Task> tasks = new CopyOnWriteArrayList<>();
        Optional<Project> optionalProject = projectRepository.findById(id);
        int[] projectNumber = new int[1];
        projectNumber[0] = 0;

        int[] lotNumber = new int[1];
        lotNumber[0] = 0;

        int[] amount = new int[1];
        amount[0] = 0;

        Task[] task = new Task[1];
        task[0] = null;

        Part[] part = new Part[1];
        part[0] = null;
        optionalProject.ifPresent(project -> {
            projectNumber[0] = project.getNumber();
            project.getProjectLists().forEach(projectList -> {
                part[0] = projectList.getPart();
                amount[0] = projectList.getAmount();
                for (int i = 0; i < amount[0]; i++) {
                    lotNumber[0] = i + 1;
                    part[0].getTermParts().forEach(termPart -> {
                        task[0] = Task.formTask(projectNumber[0],
                                part[0].getNumber(),
                                part[0].getName(),
                                termPart.getNumber(),
                                termPart.getOperationTime(),
                                lotNumber[0]);
                        Optional<Equipment> optionalEquipment = equipmentRepository.findById(termPart.getEquipment().getId());
                        optionalEquipment.ifPresent(task[0]::setEquipment);
                        Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(1);
                        optionalTaskCondition.ifPresent(task[0]::setTaskCondition);
                        task[0].setProject(project);
                        tasks.add(task[0]);
                    });
                }
            });
        });
        taskRepository.saveAll(tasks);
    }


}




