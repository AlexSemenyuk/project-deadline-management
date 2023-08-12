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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitDatabase implements CommandLineRunner {
    private final DivisionTypeRepository divisionTypeRepository;
    private final DivisionRepository divisionRepository;
    private final PartRepository partRepository;
    private final AssemblyRepository assemblyRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final CustomerRepository customerRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectListRepository projectListRepository;
    private final TechnologyPartRepository termPartRepository;
    private final TaskService taskService;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductionPlanService productionPlanService;
    private final ContractTypeRepository contractTypeRepository;
    private final ContractRepository contractRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // 1. Тип дивизиона (подготовка / производство)
        divisionTypeRepository.save( new DivisionType("Підготовка"));
        divisionTypeRepository.save(new DivisionType("Виробництво"));

        // 2. Создание дивизионов (конструкторский отдел, технологический отдел, мх-1 (механо-сборочный цех №1), мх-2, мх-3)
//        addDivision(new DivisionCommand("Design", 1));
//        addDivision(new DivisionCommand("Technology", 1));
        addDivision(new DivisionCommand("Мх-1", 2));
        addDivision(new DivisionCommand("Мх-2", 2));
        addDivision(new DivisionCommand("Мх-3", 2));

        // 3. Создание оборудования
        addEquipment(new EquipmentCommand(11, "Токарный", 1));
        addEquipment(new EquipmentCommand(12, "Фрезерный", 1));
        addEquipment(new EquipmentCommand(13, "Шлифовальный", 1));

        addEquipment(new EquipmentCommand(21, "Расточной", 2));
        addEquipment(new EquipmentCommand(22, "Фрезерный", 2));
        addEquipment(new EquipmentCommand(23, "Долбежный", 2));

        addEquipment(new EquipmentCommand(31, "Продольно-фрезерный", 3));
        addEquipment(new EquipmentCommand(32, "Фрезерный", 3));
        addEquipment(new EquipmentCommand(33, "Расточной", 3));


        // 4. Создание Parts / деталей
        partRepository.save(new Part(1, "Корпус"));
        partRepository.save(new Part(2, "Крышка"));
        partRepository.save(new Part(3, "Фланец"));
        partRepository.save(new Part(4, "Вал"));
        partRepository.save(new Part(5, "Ось"));
        partRepository.save(new Part(6, "Втулка"));
        partRepository.save(new Part(7, "Зубчатое колесо"));
        partRepository.save(new Part(8, "Шестерня"));

        // 5. Создание Assemblies / сборок


        // 6. Создание ProjectCondition / Состояние проекта (Design, Technology, Production, Archive)
        projectConditionRepository.save(new ProjectCondition("Design"));
        projectConditionRepository.save(new ProjectCondition("Technology"));
        projectConditionRepository.save(new ProjectCondition("Production"));
        projectConditionRepository.save(new ProjectCondition("Archive"));

        // 7. Создание Customer / Заказчиков
        customerRepository.save(new Customer("Запорожсталь", "Запорожье"));
        customerRepository.save(new Customer("Арселор Миттал Кривой Рог", "Кривой Рог"));
        customerRepository.save(new Customer("ArcelorMittalGalati", "Galati"));
        customerRepository.save(new Customer("U.S.Steel Kosice", "Kosice"));

        // 8. Создание состояний task (Production, Stop, Ок, Archive)
        taskConditionRepository.save(new TaskCondition("New"));
        taskConditionRepository.save(new TaskCondition("Production"));
        taskConditionRepository.save(new TaskCondition("Ок"));
        taskConditionRepository.save(new TaskCondition("Design"));
        taskConditionRepository.save(new TaskCondition("Technology"));
        taskConditionRepository.save(new TaskCondition("Archive"));

        // 9. ContractTypes
        contractTypeRepository.save( new ContractType("Metal"));
        contractTypeRepository.save( new ContractType("Component"));


        // 10. Создание Project
        // Project 1001
        addProject(new ProjectCommand(
                1001, null, 1,
                LocalDateTime.parse("2023-07-25T00:00"), LocalDateTime.parse("2023-08-20T00:00"),
                1));
        addProjectList(1, new ProjectListCommand(1, 2));        // ProjectLists
        addProjectList(1, new ProjectListCommand(3, 3));
        addDesignTerm(1, 7);                                      // DesignTerm


        addTermPart(new TechnologyPartCommand(1, 1, 1, 3));     // TermPart
        addTermPart(new TechnologyPartCommand(1, 2, 2, 2));
        addTermPart(new TechnologyPartCommand(1, 3, 3, 5));
        addTermPart(new TechnologyPartCommand(3, 1, 1, 2));
        addTermPart(new TechnologyPartCommand(3, 2, 3, 4));
        addTechnologyTerm(1, 7);                               // TechnologyTerm


        addContract(1, new ContractCommand(                                   // Contracts
                "№10000-1", "Закупка металла",
                LocalDateTime.parse("2023-08-04T00:00"),
                LocalDateTime.parse("2023-08-14T00:00"),
                1));
        addContract(1, new ContractCommand(
                "№10000-2", "Закупка металла",
                LocalDateTime.parse("2023-08-05T00:00"),
                LocalDateTime.parse("2023-08-11T00:00"),
                1));
        addContract(1, new ContractCommand(
                "№10000-3", "Закупка металла",
                LocalDateTime.parse("2023-08-09T00:00"),
                LocalDateTime.parse("2023-08-15T00:00"),
                1));

        productionPlanService.formProductionPlans(taskService.formTasks(1));



        // Project 1002
        addProject(new ProjectCommand(
                1002, null, 2,
                LocalDateTime.parse("2023-07-21T00:00"), LocalDateTime.parse("2023-08-28T00:00"),
                1));
        addProjectList(2, new ProjectListCommand(2, 3));        // ProjectLists
        addProjectList(2, new ProjectListCommand(4, 1));
        addDesignTerm(2, 5);                                      // DesignTerm

        addTermPart(new TechnologyPartCommand(2, 1, 4, 1));     // TermPart
        addTermPart(new TechnologyPartCommand(2, 2, 5, 3));
        addTermPart(new TechnologyPartCommand(2, 3, 6, 2));
        addTermPart(new TechnologyPartCommand(4, 1, 5, 2));
        addTermPart(new TechnologyPartCommand(4, 2, 6, 4));
        addTechnologyTerm(2, 7);                               // TechnologyTerm

        addContract(2, new ContractCommand(                                   // Contracts
                "№10000-4", "Закупка металла",
                LocalDateTime.parse("2023-07-27T00:00"),
                LocalDateTime.parse("2023-08-10T00:00"),
                1));

        productionPlanService.formProductionPlans(taskService.formTasks(2));

        // Project 1003
        addProject(new ProjectCommand(
                1003, null, 3,
                LocalDateTime.parse("2023-07-18T00:00"), LocalDateTime.parse("2023-08-15T00:00"),
                1));
        addProjectList(3, new ProjectListCommand(5, 3));
        addProjectList(3, new ProjectListCommand(6, 5));
        addDesignTerm(3, 8);

        addTermPart(new TechnologyPartCommand(5, 1, 3, 1));
        addTermPart(new TechnologyPartCommand(5, 2, 4, 3));
        addTermPart(new TechnologyPartCommand(5, 3, 7, 5));
        addTermPart(new TechnologyPartCommand(6, 1, 7, 2));
        addTermPart(new TechnologyPartCommand(6, 2, 9, 4));
        addTechnologyTerm(3, 8);

        addContract(3, new ContractCommand(                                   // Contracts
                "№10000-5", "Закупка металла",
                LocalDateTime.parse("2023-07-24T00:00"),
                LocalDateTime.parse("2023-08-08T00:00"),
                1));

        productionPlanService.formProductionPlans(taskService.formTasks(3));

        // Project 1004
        addProject(new ProjectCommand(
                1004, null, 4,
                LocalDateTime.parse("2023-07-15T00:00"), LocalDateTime.parse("2023-07-26T00:00"),
                1));
        addProjectList(4, new ProjectListCommand(5, 1));
        addProjectList(4, new ProjectListCommand(8, 2));
        addDesignTerm(4, 6);

        addTermPart(new TechnologyPartCommand(8, 1, 1, 2));
        addTermPart(new TechnologyPartCommand(8, 2, 7, 4));
        addTechnologyTerm(4, 6);

        addContract(4, new ContractCommand(
                "№10000-6", "Закупка металла",
                LocalDateTime.parse("2023-07-25T00:00"),
                LocalDateTime.parse("2023-08-03T00:00"),
                1));

        productionPlanService.formProductionPlans(taskService.formTasks(4));

    }




    private void addDivision(DivisionCommand command) {
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(command.divisionTypeId());
        optionalDivisionType.ifPresent(divisionType -> {
            Division division = Division.fromCommand(command);
            divisionType.setDivisions(new ArrayList<>());
            division.setDivisionType(divisionType);
            divisionRepository.save(division);
        });
    }


    private void addEquipment(EquipmentCommand command) {
        Optional<Division> optionalDivision = divisionRepository.findById(command.divisionId());
        optionalDivision.ifPresent(division -> {
            Equipment equipment = Equipment.fromCommand(command);
            equipment.setDivision(division);
            equipmentRepository.save(equipment);
        });
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
            projectRepository.save(project);
        }
    }

    private void addProjectList(int projectId, ProjectListCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        optionalProject.ifPresent(project -> {
            Optional<Part> optionalPart = partRepository.findById(command.partId());
            optionalPart.ifPresent(part -> {
                ProjectList projectList = ProjectList.fromCommand(command);
                projectList.setPart(part);
                projectListRepository.save(projectList);
                project.getProjectLists().add(projectList);
                projectRepository.save(project);
            });
        });
    }

    private void addDesignTerm(int projectId, int designTerm) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        optionalProject.ifPresent(project -> {
            project.setDesignTerm(designTerm);
            projectRepository.save(project);
        });
    }

    private void addTermPart(TechnologyPartCommand command) {
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()) {
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            TechnologyPart termPart = TechnologyPart.fromCommand(command);
            termPart.setPart(part);
            termPart.setEquipment(equipment);
            termPartRepository.save(termPart);
        }
    }

    private void addTechnologyTerm(int projectId, int technologyTerm) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()){
            Project project = optionalProject.get();
            project.setTechnologyTerm(technologyTerm);
            projectRepository.save(project);
        }
    }

    private void addContract(int projectId, ContractCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(command.contractTypeId());

        if (optionalContractType.isPresent() && optionalProject.isPresent()){
            ContractType contractType = optionalContractType.get();
            Project project = optionalProject.get();
            Contract contract = Contract.fromCommand(command);
            contract.setContractType(contractType);
            contract.setProject(project);
            contractRepository.save(contract);
        }
    }
}

