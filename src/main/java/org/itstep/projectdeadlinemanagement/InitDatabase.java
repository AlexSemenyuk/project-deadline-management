package org.itstep.projectdeadlinemanagement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.itstep.projectdeadlinemanagement.command.*;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitDatabase implements CommandLineRunner {
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final DivisionRepository divisionRepository;
    private final PartRepository partRepository;
    private final AssemblyRepository assemblyRepository;
    private final AssemblyListRepository assemblyListRepository;
    private final PartListRepository partListRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final CustomerRepository customerRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectListRepository projectListRepository;
    private final TechnologyAssemblyRepository technologyAssemblyRepository;
    private final TechnologyPartRepository technologyPartRepository;
    private final TaskService taskService;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanService productionPlanService;
    private final ContractTypeRepository contractTypeRepository;
    private final ContractRepository contractRepository;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // 1. Тип дивизиона (подготовка / производство)

        equipmentTypeRepository.save(new EquipmentType("Виробництво"));
        equipmentTypeRepository.save(new EquipmentType("Складання вузлів"));

        // 2. Создание дивизионов (конструкторский отдел, технологический отдел, мх-1 (механо-сборочный цех №1), мх-2, мх-3)
//        addDivision(new DivisionCommand("Design", 1));
//        addDivision(new DivisionCommand("Technology", 1));
        divisionRepository.save(new Division("Мх-1"));
        divisionRepository.save(new Division("Мх-2"));
        divisionRepository.save(new Division("Мх-3"));
//        addDivision(new DivisionCommand("Мх-2"));
//        addDivision(new DivisionCommand("Мх-3"));
//        private void addDivision(DivisionCommand command) {
//            Division division = Division.fromCommand(command);
//            divisionRepository.save(division);
//        }

        // 3. Создание оборудования
        addEquipment(new EquipmentCommand(11, "Токарний", 1, 1));
        addEquipment(new EquipmentCommand(12, "Фрезерний", 1, 1));
        addEquipment(new EquipmentCommand(13, "Шліфувальний", 1, 1));
        addEquipment(new EquipmentCommand(14, "Складання вузлів", 1, 2));

        addEquipment(new EquipmentCommand(21, "Расточной", 2, 1));
        addEquipment(new EquipmentCommand(22, "Фрезерний", 2, 1));
        addEquipment(new EquipmentCommand(23, "Долбіжний", 2, 1));
        addEquipment(new EquipmentCommand(24, "Складання вузлів", 2, 2));

        addEquipment(new EquipmentCommand(31, "Поздовжньо-фрезерний", 3, 1));
        addEquipment(new EquipmentCommand(32, "Токарний", 3, 1));
        addEquipment(new EquipmentCommand(33, "Расточной", 3, 1));
        addEquipment(new EquipmentCommand(34, "Складання вузлів", 3, 2));


        // 4. Создание Parts / деталей
        partRepository.save(new Part(1, "Корпус"));
        partRepository.save(new Part(2, "Кришка"));
        partRepository.save(new Part(3, "Фланець"));
        partRepository.save(new Part(4, "Вал"));
        partRepository.save(new Part(5, "Вісь"));
        partRepository.save(new Part(6, "Втулка"));
        partRepository.save(new Part(7, "Зубчасте колесо"));
        partRepository.save(new Part(8, "Шестірня"));

        // 5. Создание Assemblies / сборок
        assemblyRepository.save(new Assembly(101, "Корпус у зборі"));
        assemblyRepository.save(new Assembly(102, "Фланець у зборі"));
        assemblyRepository.save(new Assembly(103, "Вал у зборі"));
        assemblyRepository.save(new Assembly(104, "Кришка у зборі"));

        addAssemblyList(1, new AssemblyListCommand(2, 2));
        addAssemblyList(1, new AssemblyListCommand(3, 1));

        addPartList(1, new PartListCommand(1, 2));
        addPartList(1, new PartListCommand(6, 4));

        addPartList(2, new PartListCommand(3, 1));
        addPartList(2, new PartListCommand(5, 1));

        addPartList(3, new PartListCommand(4, 1));
        addPartList(3, new PartListCommand(6, 2));

        addPartList(4, new PartListCommand(2, 1));
        addPartList(4, new PartListCommand(6, 2));

        // 6. Создание ProjectCondition / Состояние проекта (Design, Technology, Production, Archive)
        projectConditionRepository.save(new ProjectCondition("Design"));
        projectConditionRepository.save(new ProjectCondition("Technology"));
        projectConditionRepository.save(new ProjectCondition("Production"));
        projectConditionRepository.save(new ProjectCondition("Archive"));

        // 7. Создание Customer / Заказчиков
        customerRepository.save(new Customer("Запоріжсталь", "Запоріжжя"));
        customerRepository.save(new Customer("Арселорміттал Кривий Ріг", "Кривий Ріг"));
        customerRepository.save(new Customer("ArcelorMittalGalati", "Galati"));
        customerRepository.save(new Customer("U.S.Steel Kosice", "Kosice"));

        // 8. Создание состояний task (Production, Stop, Ок, Archive)
        taskConditionRepository.save(new TaskCondition("New"));
        taskConditionRepository.save(new TaskCondition("Production"));
        taskConditionRepository.save(new TaskCondition("Ок"));
        taskConditionRepository.save(new TaskCondition("Design"));
        taskConditionRepository.save(new TaskCondition("Technology"));
        taskConditionRepository.save(new TaskCondition("Archive"));

        // 9. Создание taskType (Part, Assembly)
        taskTypeRepository.save(new TaskType("Деталь"));
        taskTypeRepository.save(new TaskType("Вузол"));


        // 10. ContractTypes
        contractTypeRepository.save(new ContractType("Матеріали"));
        contractTypeRepository.save(new ContractType("Комплектуючі вироби"));


        // 11. Создание Project
        // Project 1001
        addProject(new ProjectCommand(
                1001, null, 1,
                LocalDateTime.parse("2023-06-05T00:00"), LocalDateTime.parse("2023-07-07T00:00"),
                4));
        // DesignTerm
        addDesignTerm(1, 5);

        // ProjectLists
        addAssemblyListToProject(1, new AssemblyListCommand(2, 3));
        addAssemblyListToProject(1, new AssemblyListCommand(3, 2));

        addPartListToProject(1, new PartListCommand(7, 1));
        addPartListToProject(1, new PartListCommand(8, 2));

        // TermPart
        createTechnologyPart(new TechnologyPartCommand(3, 1, 1, 3));
        createTechnologyPart(new TechnologyPartCommand(3, 2, 2, 2));

        createTechnologyPart(new TechnologyPartCommand(4, 1, 2, 4));
        createTechnologyPart(new TechnologyPartCommand(4, 2, 3, 1));

        createTechnologyPart(new TechnologyPartCommand(5, 1, 1, 3));
        createTechnologyPart(new TechnologyPartCommand(5, 2, 2, 2));
        createTechnologyPart(new TechnologyPartCommand(5, 3, 3, 5));

        createTechnologyPart(new TechnologyPartCommand(6, 1, 1, 4));
        createTechnologyPart(new TechnologyPartCommand(6, 2, 3, 2));

        createTechnologyPart(new TechnologyPartCommand(7, 1, 1, 4));
        createTechnologyPart(new TechnologyPartCommand(7, 2, 2, 3));
        createTechnologyPart(new TechnologyPartCommand(7, 3, 3, 5));

        createTechnologyPart(new TechnologyPartCommand(8, 1, 1, 4));
        createTechnologyPart(new TechnologyPartCommand(8, 2, 3, 3));

        createTechnologyAssembly(new TechnologyAssemblyCommand(2, 1, 4, 1));
        createTechnologyAssembly(new TechnologyAssemblyCommand(3, 1, 4, 4));

        addTechnologyTerm(1, 5);                               // TechnologyTerm

        addContract(1, new ContractCommand(                                   // Contracts material
                "№10000-1", "Закупівля металу",
                LocalDateTime.parse("2023-06-08T00:00"),
                LocalDateTime.parse("2023-06-14T00:00"),
                1));
        addContract(1, new ContractCommand(
                "№10000-2", "Закупівля металу",
                LocalDateTime.parse("2023-06-09T00:00"),
                LocalDateTime.parse("2023-06-15T00:00"),
                1));
        addContract(1, new ContractCommand(
                "№10000-3", "Закупівля металу",
                LocalDateTime.parse("2023-06-09T00:00"),
                LocalDateTime.parse("2023-06-17T00:00"),
                1));

        addContract(1, new ContractCommand(                                   // Contracts component
                "№10000-4", "Закупівля комплектуючих виробів",
                LocalDateTime.parse("2023-06-08T00:00"),
                LocalDateTime.parse("2023-06-19T00:00"),
                2));
        addContract(1, new ContractCommand(
                "№10000-5", "Закупівля комплектуючих виробів",
                LocalDateTime.parse("2023-06-09T00:00"),
                LocalDateTime.parse("2023-06-19T00:00"),
                2));


        productionPlanService.formProductionPlans(taskService.formTasks(1));
        changeProjectCondition(1,4);
        changeTaskCondition(1);


        // Project 1002
        addProject(new ProjectCommand(
                1002, null, 2,
                LocalDateTime.parse("2023-07-25T00:00"), LocalDateTime.parse("2023-09-25T00:00"),
                1));
        // DesignTerm
        addDesignTerm(2, 7);
        // ProjectLists
        addAssemblyListToProject(2, new AssemblyListCommand(1, 2));
        addAssemblyListToProject(2, new AssemblyListCommand(4, 2));
        addPartListToProject(2, new PartListCommand(5, 1));
        addPartListToProject(2, new PartListCommand(6, 2));

        // TermPart
        createTechnologyPart(new TechnologyPartCommand(1, 1, 1, 5));
        createTechnologyPart(new TechnologyPartCommand(1, 2, 3, 3));

        createTechnologyPart(new TechnologyPartCommand(2, 1, 1, 7));
        createTechnologyPart(new TechnologyPartCommand(2, 2, 3, 8));

        createTechnologyAssembly(new TechnologyAssemblyCommand(1, 1, 4, 2));
        createTechnologyAssembly(new TechnologyAssemblyCommand(4, 1, 4, 3));

        addTechnologyTerm(2, 7);                               // TechnologyTerm

        addContract(2, new ContractCommand(                                   // Contracts material
                "№10000-6", "Закупівля металу",
                LocalDateTime.parse("2023-08-04T00:00"),
                LocalDateTime.parse("2023-08-14T00:00"),
                1));
        addContract(2, new ContractCommand(
                "№10000-7", "Закупівля металу",
                LocalDateTime.parse("2023-08-05T00:00"),
                LocalDateTime.parse("2023-08-11T00:00"),
                1));
        addContract(2, new ContractCommand(
                "№10000-8", "Закупівля металу",
                LocalDateTime.parse("2023-08-09T00:00"),
                LocalDateTime.parse("2023-08-15T00:00"),
                1));

        addContract(2, new ContractCommand(                                   // Contracts component
                "№10000-9", "Закупівля комплектуючих виробів",
                LocalDateTime.parse("2023-08-10T00:00"),
                LocalDateTime.parse("2023-08-27T00:00"),
                2));
        addContract(2, new ContractCommand(
                "№10000-10", "Закупівля комплектуючих виробів",
                LocalDateTime.parse("2023-08-09T00:00"),
                LocalDateTime.parse("2023-09-05T00:00"),
                2));
//
        productionPlanService.formProductionPlans(taskService.formTasks(2));




        // Project 1002
//        addProject(new ProjectCommand(
//                1002, null, 2,
//                LocalDateTime.parse("2023-07-21T00:00"), LocalDateTime.parse("2023-08-28T00:00"),
//                1));
//        addProjectList(2, new ProjectListCommand(2, 3));        // ProjectLists
//        addProjectList(2, new ProjectListCommand(4, 1));
//        addDesignTerm(2, 5);                                      // DesignTerm
//
//        addTermPart(new TechnologyPartCommand(2, 1, 4, 1));     // TermPart
//        addTermPart(new TechnologyPartCommand(2, 2, 5, 3));
//        addTermPart(new TechnologyPartCommand(2, 3, 6, 2));
//        addTermPart(new TechnologyPartCommand(4, 1, 5, 2));
//        addTermPart(new TechnologyPartCommand(4, 2, 6, 4));
//        addTechnologyTerm(2, 7);                               // TechnologyTerm
//
//        addContract(2, new ContractCommand(                                   // Contracts
//                "№10000-4", "Закупка металла",
//                LocalDateTime.parse("2023-07-27T00:00"),
//                LocalDateTime.parse("2023-08-10T00:00"),
//                1));
//
//        productionPlanService.formProductionPlans(taskService.formTasks(2));

        // Project 1003
//        addProject(new ProjectCommand(
//                1003, null, 3,
//                LocalDateTime.parse("2023-07-18T00:00"), LocalDateTime.parse("2023-08-15T00:00"),
//                1));
//        addProjectList(3, new ProjectListCommand(5, 3));
//        addProjectList(3, new ProjectListCommand(6, 5));
//        addDesignTerm(3, 8);
//
//        addTermPart(new TechnologyPartCommand(5, 1, 3, 1));
//        addTermPart(new TechnologyPartCommand(5, 2, 4, 3));
//        addTermPart(new TechnologyPartCommand(5, 3, 7, 5));
//        addTermPart(new TechnologyPartCommand(6, 1, 7, 2));
//        addTermPart(new TechnologyPartCommand(6, 2, 9, 4));
//        addTechnologyTerm(3, 8);
//
//        addContract(3, new ContractCommand(                                   // Contracts
//                "№10000-5", "Закупка металла",
//                LocalDateTime.parse("2023-07-24T00:00"),
//                LocalDateTime.parse("2023-08-08T00:00"),
//                1));

//        productionPlanService.formProductionPlans(taskService.formTasks(3));

        // Project 1004
//        addProject(new ProjectCommand(
//                1004, null, 4,
//                LocalDateTime.parse("2023-07-15T00:00"), LocalDateTime.parse("2023-07-26T00:00"),
//                1));
//        addProjectList(4, new ProjectListCommand(5, 1));
//        addProjectList(4, new ProjectListCommand(8, 2));
//        addDesignTerm(4, 6);
//
//        addTermPart(new TechnologyPartCommand(8, 1, 1, 2));
//        addTermPart(new TechnologyPartCommand(8, 2, 7, 4));
//        addTechnologyTerm(4, 6);
//
//        addContract(4, new ContractCommand(
//                "№10000-6", "Закупка металла",
//                LocalDateTime.parse("2023-07-25T00:00"),
//                LocalDateTime.parse("2023-08-03T00:00"),
//                1));
//
//        productionPlanService.formProductionPlans(taskService.formTasks(4));

    }




    private void addEquipment(EquipmentCommand command) {
        Optional<Division> optionalDivision = divisionRepository.findById(command.divisionId());
        Optional<EquipmentType> optionalEquipmentType = equipmentTypeRepository.findById(command.equipmentTypeId());
        if (optionalDivision.isPresent() && optionalEquipmentType.isPresent()){
            Equipment equipment = Equipment.fromCommand(command);

            Division division = optionalDivision.get();
            equipment.setDivision(division);

            EquipmentType equipmentType = optionalEquipmentType.get();
            equipment.setEquipmentType(equipmentType);

            equipmentRepository.save(equipment);

        }
    }

    private void addPartList(int assemblyId, PartListCommand command) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(assemblyId);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        if (optionalAssembly.isPresent() && optionalPart.isPresent()) {
            Assembly assembly = optionalAssembly.get();
            Part part = optionalPart.get();

            PartList partList = PartList.fromCommand(command);
            partList.setPart(part);
            partList.addAssembly(assembly);
            partListRepository.save(partList);
        }
    }

    private void addAssemblyList(int assemblyId, AssemblyListCommand command) {
        Optional<Assembly> optionalAssemblyEntry = assemblyRepository.findById(assemblyId);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        if (optionalAssemblyEntry.isPresent() && optionalAssembly.isPresent()) {
            Assembly assemblyEntry = optionalAssemblyEntry.get();
            Assembly assembly = optionalAssembly.get();

            AssemblyList assemblyList = AssemblyList.fromCommand(command);
            assemblyList.setAssembly(assembly);
            assemblyList.addAssemblyListEntry(assemblyEntry);
            assemblyListRepository.save(assemblyList);
        }
    }


    private void addProject(ProjectCommand command) {
        Optional<Customer> optionalCustomer = customerRepository.findById(command.customerId());
        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(1);
        if (optionalCustomer.isPresent() && optionalProjectCondition.isPresent()) {
            Customer customer = optionalCustomer.get();
            ProjectCondition projectCondition = optionalProjectCondition.get();
            Project project = Project.fromCommand(command);
            project.setCustomer(customer);
            project.setProjectCondition(projectCondition);

            ProjectList projectList = new ProjectList();
            project.setProjectList(projectList);
            projectRepository.save(project);
        }
    }

    private void addAssemblyListToProject(Integer projectId, AssemblyListCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        if (optionalProject.isPresent() && optionalAssembly.isPresent()) {
            ProjectList projectList = optionalProject.get().getProjectList();
            Assembly assembly = optionalAssembly.get();

            AssemblyList assemblyList = AssemblyList.fromCommand(command);
            assemblyList.setAssembly(assembly);
            assemblyList.addProjectList(projectList);
            assemblyListRepository.save(assemblyList);
        }
    }

    private void addPartListToProject(Integer projectId, PartListCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        if (optionalProject.isPresent() && optionalPart.isPresent()){
            ProjectList projectList = optionalProject.get().getProjectList();
            Part part = optionalPart.get();

            PartList partList = PartList.fromCommand(command);
            partList.setPart(part);
            partList.addProjectList(projectList);
            partListRepository.save(partList);
        }
    }

    private void addDesignTerm(int projectId, int designTerm) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        optionalProject.ifPresent(project -> {
            project.setDesignTerm(designTerm);
            projectRepository.save(project);
        });
    }

    private void createTechnologyAssembly(TechnologyAssemblyCommand command) {
            Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
            Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
            if (optionalAssembly.isPresent() && optionalEquipment.isPresent()) {
                Assembly assembly = optionalAssembly.get();
                Equipment equipment = optionalEquipment.get();
                TechnologyAssembly technologyAssembly = TechnologyAssembly.fromCommand(command);
                technologyAssembly.setAssembly(assembly);
                technologyAssembly.setEquipment(equipment);
                technologyAssemblyRepository.save(technologyAssembly);
            }
    }

    private void createTechnologyPart(TechnologyPartCommand command) {
            Optional<Part> optionalPart = partRepository.findById(command.partId());
            Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
            if (optionalPart.isPresent() && optionalEquipment.isPresent()) {
                Part part = optionalPart.get();
                Equipment equipment = optionalEquipment.get();
                TechnologyPart technologyPart = TechnologyPart.fromCommand(command);
                technologyPart.setPart(part);
                technologyPart.setEquipment(equipment);
                technologyPartRepository.save(technologyPart);
            }
    }

    private void addTechnologyTerm(int projectId, int technologyTerm) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setTechnologyTerm(technologyTerm);
            projectRepository.save(project);
        }
    }

    private void addContract(int projectId, ContractCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(command.contractTypeId());

        if (optionalContractType.isPresent() && optionalProject.isPresent()) {
            ContractType contractType = optionalContractType.get();
            Project project = optionalProject.get();
            Contract contract = Contract.fromCommand(command);
            contract.setContractType(contractType);
            contract.setProject(project);
            contractRepository.save(contract);
        }
    }

    private void changeProjectCondition(int projectId, int projectConditionId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(projectConditionId);

        if (optionalProject.isPresent() && optionalProjectCondition.isPresent()){
            Project project = optionalProject.get();
            ProjectCondition projectCondition = optionalProjectCondition.get();
            project.setProjectCondition(projectCondition);
            projectRepository.save(project);
        }

    }
    private void changeTaskCondition(int projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(6);

        if (optionalProject.isPresent() && optionalTaskCondition.isPresent()){
            Project project = optionalProject.get();
            TaskCondition taskCondition = optionalTaskCondition.get();
            if (project.getProjectCondition().getName().equals("Archive")){
                if (!project.getTasks().isEmpty()){
                    for (Task task: project.getTasks()){
                        task.setTaskCondition(taskCondition);
                    }
                }
            }
        }

    }
}

