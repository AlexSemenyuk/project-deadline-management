package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectListService projectListService;

    public List<Task> formTasks(Integer projectId) {
        List<Task> partTasks = new ArrayList<>();
        List<Task> assemblyTasks = new ArrayList<>();
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            partTasks = formPartTasks(project);
            taskRepository.saveAll(partTasks);

            Task partTaskTMP = getLastPartTask(project, partTasks);
//                    Task partTaskTMP = partTasks.get(partTasks.size() - 1);
            LocalDateTime finishPartProduction = TimeService.localDateTimeAddHours(partTaskTMP.getStartProduction(), partTaskTMP.getOperationTime());
            System.out.println("finishPartProduction = " + finishPartProduction);
            assemblyTasks = formAssemblyTasks(project, finishPartProduction);
            taskRepository.saveAll(assemblyTasks);
//            getAllListsWithAmountOnProject(project.getProjectList());

            partTasks.addAll(assemblyTasks);

        }

        return partTasks;
    }



    public List<Task> formPartTasks(Project project) {
        Integer projectNumberTMP = project.getNumber();
        LocalDateTime startPartProduction = formStartPartProduction(project);
        List<PartList> partLists = projectListService.getAllPartListsWithAmountOnProject(project.getProjectList());

        List<Task> tasks = new CopyOnWriteArrayList<>();
        LocalDateTime start;
//        System.out.println("formPartTasks ----");
        for (PartList partList : partLists) {
            Part partTMP = partList.getPart();
//            System.out.println("partList10 = " + partList.getPart().getNumber() + " - " +
//                    partList.getPart().getName() + " x " +partList.getAmount());
            for (int i = 0; i < partList.getAmount(); i++) {
                int lotNumberTMP = i + 1;
                start = startPartProduction;
                for (TechnologyPart technologyPart : partTMP.getTechnologyParts()) {
                    Task task = new Task(projectNumberTMP,
                            partTMP.getNumber(),
                            partTMP.getName(),
                            technologyPart.getNumber(),
                            technologyPart.getOperationTime(),
                            lotNumberTMP,
                            start
                    );
                    Optional<Equipment> optionalEquipment = equipmentRepository.findById(technologyPart.getEquipment().getId());
                    optionalEquipment.ifPresent(task::setEquipment);
                    Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(1);
                    optionalTaskCondition.ifPresent(task::setTaskCondition);
                    Optional<TaskType> optionalTaskType = taskTypeRepository.findById(1);
                    optionalTaskType.ifPresent(task::setTaskType);

                    task.setProject(project);

                    tasks.add(task);
                    start = TimeService.localDateTimeAddHours(start, technologyPart.getOperationTime());
//                    start = start.plusHours(technologyPart.getOperationTime());
                }
            }
        }
        return tasks;
    }

    public List<Task> formAssemblyTasks(Project project, LocalDateTime finishPartProduction) {
        Integer projectNumberTMP = project.getNumber();
        LocalDateTime startAssemblyProduction = formStartAssemblyProduction(project, finishPartProduction);       ///////////////////////////
        List<AssemblyList> assemblyLists = projectListService.getAllAssemblyListsWithAmountOnProject(project.getProjectList());

        List<Task> tasks = new CopyOnWriteArrayList<>();
        LocalDateTime start;
        for (AssemblyList assemblyList : assemblyLists) {
            Assembly assemblyTMP = assemblyList.getAssembly();

            for (int i = 0; i < assemblyList.getAmount(); i++) {
                int lotNumberTMP = i + 1;
                start = startAssemblyProduction;
                for (TechnologyAssembly technologyAssembly : assemblyTMP.getTechnologyAssemblies()) {
                    Task task = new Task(projectNumberTMP,
                            assemblyTMP.getNumber(),
                            assemblyTMP.getName(),
                            technologyAssembly.getNumber(),
                            technologyAssembly.getOperationTime(),
                            lotNumberTMP,
                            start
                    );
                    Optional<Equipment> optionalEquipment = equipmentRepository.findById(technologyAssembly.getEquipment().getId());
                    optionalEquipment.ifPresent(task::setEquipment);
                    Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(1);
                    optionalTaskCondition.ifPresent(task::setTaskCondition);
                    Optional<TaskType> optionalTaskType = taskTypeRepository.findById(2);
                    optionalTaskType.ifPresent(task::setTaskType);

                    task.setProject(project);
                    tasks.add(task);
                    start = TimeService.localDateTimeAddHours(start, technologyAssembly.getOperationTime());
//                    start = start.plusHours(technologyAssembly.getOperationTime());
                }
            }
        }
        return tasks;
    }


    public LocalDateTime formStartPartProduction(Project project) {
        LocalDateTime startPartProduction;
        // DesignTerm
        LocalDateTime startTMP = TimeService.excludeWeekend(project.getStart());
        LocalDateTime deadlineTMP = TimeService.localDateTimeAddDays(startTMP, project.getDesignTerm() - 1);
//        LocalDateTime deadlineTMP = startAddDays(startTMP, project.getDesignTerm());

        // TechnologyTerm
        startTMP = TimeService.excludeWeekend(deadlineTMP.plusDays(1));
        deadlineTMP = TimeService.localDateTimeAddDays(startTMP, project.getDesignTerm() - 1);
//        deadlineTMP = startAddDays(startTMP, project.getDesignTerm());

        // Max from contract.getDeadline
        List<Contract> contracts = project.getContracts();
        LocalDateTime contractTMP = project.getStart();
        for (Contract contract : contracts) {
            if (contract.getContractType().getId() == 1 && contract.getDeadline().isAfter(contractTMP)) {
                contractTMP = contract.getDeadline();
            }
        }

        if (deadlineTMP.isAfter(contractTMP)) {
            startPartProduction = TimeService.excludeWeekend(deadlineTMP.plusDays(1));
        } else {
            startPartProduction = TimeService.excludeWeekend(contractTMP.plusDays(1));
        }
        return startPartProduction;
    }

    public Task getLastPartTask(Project project, List<Task> partTasks) {
        Task lastTask = null;
        LocalDateTime dateTMP = project.getStart();
        for (Task task: partTasks){
            LocalDateTime taskDate = TimeService.localDateTimeAddHours(task.getStartProduction(), task.getOperationTime());
//            System.out.println("taskDate = " + taskDate);
            if (taskDate.isAfter(dateTMP)){
                dateTMP = taskDate;
                lastTask = task;
            }
        }
        return lastTask;
    }

    public LocalDateTime formStartAssemblyProduction(Project project, LocalDateTime finishPartProduction) {
        LocalDateTime startAssemblyProduction;

        // Max from contract.getDeadline
        List<Contract> contracts = project.getContracts();
        LocalDateTime contractTMP = project.getStart();
        for (Contract contract : contracts) {
            if (contract.getContractType().getId() == 2 && contract.getDeadline().isAfter(contractTMP)) {
                contractTMP = contract.getDeadline();
            }
        }
        if (finishPartProduction.isAfter(contractTMP)) {
            startAssemblyProduction = TimeService.excludeWeekend(finishPartProduction.plusDays(1));
        } else {
            startAssemblyProduction = TimeService.excludeWeekend(contractTMP.plusDays(1));
        }
        return startAssemblyProduction;
    }

}






