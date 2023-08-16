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
    private final ProjectListService projectListService;

    public List<Task> formTasks(Integer id) {

        List<Task> partTasks = new CopyOnWriteArrayList<>();
        List<Task> assemblyTasks = new CopyOnWriteArrayList<>();
        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            partTasks = formPartTasks(project);
            taskRepository.saveAll(partTasks);

            assemblyTasks = formAssemblyTasks(project);
            taskRepository.saveAll(assemblyTasks);
//            getAllListsWithAmountOnProject(project.getProjectList());

            partTasks.addAll(assemblyTasks);

        }

        return partTasks;
    }

    public List<Task> formPartTasks(Project project) {
        Integer projectNumberTMP = project.getNumber();
        LocalDateTime startProduction = formStartProduction(project);
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
                start = startProduction;
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

                    task.setProject(project);
                    tasks.add(task);
                    start = TimeService.localDateTimeAddHours(start, technologyPart.getOperationTime());
                    start = start.plusHours(technologyPart.getOperationTime());
                }
            }
        }
        return tasks;
    }

    public List<Task> formAssemblyTasks(Project project) {
        Integer projectNumberTMP = project.getNumber();
        LocalDateTime startProduction = formStartProduction(project);
        List<AssemblyList> assemblyLists = projectListService.getAllAssemblyListsWithAmountOnProject(project.getProjectList());

        List<Task> tasks = new CopyOnWriteArrayList<>();
        LocalDateTime start;
        for (AssemblyList assemblyList : assemblyLists) {
            Assembly assemblyTMP = assemblyList.getAssembly();

            for (int i = 0; i < assemblyList.getAmount(); i++) {
                int lotNumberTMP = i + 1;
                start = startProduction;
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

                    task.setProject(project);
                    tasks.add(task);
                    start = TimeService.localDateTimeAddHours(start, technologyAssembly.getOperationTime());
                    start = start.plusHours(technologyAssembly.getOperationTime());
                }
            }
        }
        return tasks;
    }


    public LocalDateTime formStartProduction(Project project) {
        LocalDateTime startProduction;
        // DesignTerm
        LocalDateTime startTMP = TimeService.excludeWeekend(project.getStart());
        LocalDateTime deadlineTMP = TimeService.localDateTimeAddDays(startTMP, project.getDesignTerm());
//        LocalDateTime deadlineTMP = startAddDays(startTMP, project.getDesignTerm());

        // TechnologyTerm
        startTMP = TimeService.excludeWeekend(deadlineTMP.plusDays(1));
        deadlineTMP = TimeService.localDateTimeAddDays(startTMP, project.getDesignTerm());
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
            startProduction = TimeService.excludeWeekend(deadlineTMP.plusDays(1));
        } else {
            startProduction = TimeService.excludeWeekend(contractTMP.plusDays(1));
        }
        return startProduction;
    }


    public void getAllListsWithAmountOnProject(ProjectList projectList) {
        System.out.println(" Проект");
        int count;
        List<AssemblyList> assemblyLists = projectList.getAssemblyLists();
        List<AssemblyList> rezultAssemblyLists = new CopyOnWriteArrayList<>();
        List<PartList> rezultPartLists = new CopyOnWriteArrayList<>();

        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                Assembly assembly = assemblyList.getAssembly();
                count = assemblyList.getAmount();
                extractAllLists(assembly.getAssemblyListsEntry(), rezultAssemblyLists, rezultPartLists, count);

                AssemblyList newAssemblyList = new AssemblyList();
                newAssemblyList.setId(assemblyList.getId());
                newAssemblyList.setAssembly(assemblyList.getAssembly());
                newAssemblyList.setAmount(assemblyList.getAmount());
                System.out.println("Сборки projectList = " + newAssemblyList.getAssembly().getNumber() + " - " +
                                    newAssemblyList.getAssembly().getName() + " x " + newAssemblyList.getAmount());
                rezultAssemblyLists.add(assemblyList);

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        int amountTmp = partList.getAmount();
//                        System.out.println("partList2-1 = " + partList.getPart().getNumber() + " - " +
//                                partList.getPart().getName() + " x " + partList.getAmount());
                        PartList newPartList = new PartList();
                        newPartList.setId(partList.getId());
                        newPartList.setPart(partList.getPart());
                        newPartList.setAmount(amountTmp * count);
                        System.out.println("Детали сборок projectList = " + newPartList.getPart().getNumber() + " - " +
                                newPartList.getPart().getName() + " x " + newPartList.getAmount());
                        rezultPartLists.add(newPartList);
                    }
                }
            }
        }
        System.out.println("Детали projectList");
        if (!projectList.getPartLists().isEmpty()){
            for (PartList partList: projectList.getPartLists()){

                PartList newPartList = new PartList();
                newPartList.setId(partList.getId());
                newPartList.setPart(partList.getPart());
                newPartList.setAmount(partList.getAmount());
                System.out.println("Детали projectList = " + partList.getPart().getNumber() + " - " +
                        partList.getPart().getName() + " x " +partList.getAmount());
                rezultPartLists.add(newPartList);
            }
        }
    }

    private void extractAllLists(List<AssemblyList> assemblyLists, List<AssemblyList> rezultAssemblyLists, List<PartList> rezultPartLists, int count) {
        int countLocal;
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                countLocal = count * assemblyList.getAmount();
                Assembly assembly = assemblyList.getAssembly();
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                if (!assembly.getAssemblyListsEntry().isEmpty()) {
                    extractAllLists(assemblies, rezultAssemblyLists, rezultPartLists,  countLocal);
                }

                int amountAssemblyTmp = assemblyList.getAmount();

                AssemblyList newAssemblyList = new AssemblyList();
                newAssemblyList.setId(assemblyList.getId());
                newAssemblyList.setAssembly(assemblyList.getAssembly());
                newAssemblyList.setAmount(amountAssemblyTmp * count);

                System.out.println("Входящая сборка = " + newAssemblyList.getAssembly().getNumber() + " - " +
                        newAssemblyList.getAssembly().getName() + " x " + newAssemblyList.getAmount());
                rezultAssemblyLists.add(newAssemblyList);

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        int amountPartTmp = partList.getAmount();
//                        System.out.println("partList3-1 = " + partList.getPart().getNumber() + " - " +
//                                partList.getPart().getName() + " x " +partList.getAmount());
                        PartList newPartList = new PartList();
                        newPartList.setId(partList.getId());
                        newPartList.setPart(partList.getPart());
                        newPartList.setAmount(amountPartTmp * countLocal);

                        System.out.println("Входящая деталь = " + newPartList.getPart().getNumber() + " - " +
                                newPartList.getPart().getName() + " x " +newPartList.getAmount());
                        rezultPartLists.add(newPartList);
                    }
                }
            }
        }
    }

}






